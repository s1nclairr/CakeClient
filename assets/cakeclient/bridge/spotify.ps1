$ErrorActionPreference = 'Stop'
try {
[Console]::OutputEncoding = New-Object System.Text.UTF8Encoding($false)
Add-Type -AssemblyName System.Runtime.WindowsRuntime
$managerType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType=WindowsRuntime]
$propertiesType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties, Windows.Media.Control, ContentType=WindowsRuntime]
$script:asTask = [System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object {
    $_.Name -eq 'AsTask' -and $_.IsGenericMethod -and $_.GetGenericArguments().Count -eq 1 -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1'
} | Select-Object -First 1
function Await-Result($operation, $type) {
    $task = $script:asTask.MakeGenericMethod($type).Invoke($null, @($operation))
    if (-not $task.Wait(4000)) { throw 'Media session timeout' }
    return $task.Result
}
    $manager = Await-Result ($managerType::RequestAsync()) $managerType
    $props = $null
    $lastMetadata = [DateTimeOffset]::MinValue
    $lastSession = ''; $lastArtKey = ''; $art = ''
    while ($true) {
        try {
            $session = $manager.GetSessions() | Where-Object { $_.SourceAppUserModelId -match 'Spotify' } | Select-Object -First 1
            if ($null -eq $session) {
                $props = $null
                [Console]::WriteLine('{"ready":false}')
            } else {
                $now = [DateTimeOffset]::UtcNow
                if ($null -eq $props -or ($now - $lastMetadata).TotalMilliseconds -ge 1000 -or $lastSession -ne $session.SourceAppUserModelId) {
                    $props = Await-Result ($session.TryGetMediaPropertiesAsync()) $propertiesType
                    $lastMetadata = $now
                    $lastSession = $session.SourceAppUserModelId
                }
                $art = ''
                $artKey = $props.Title + '|' + $props.Artist + '|' + $props.AlbumTitle
                if ($artKey -ne $lastArtKey) {
                    try {
                        if ($null -ne $props.Thumbnail) {
                            $streamType = [Windows.Storage.Streams.IRandomAccessStreamWithContentType, Windows.Storage.Streams, ContentType=WindowsRuntime]
                            $stream = Await-Result ($props.Thumbnail.OpenReadAsync()) $streamType
                            try {
                                if ($stream.Size -gt 0 -and $stream.Size -lt 1000000) {
                                    $readerType = [Windows.Storage.Streams.DataReader, Windows.Storage.Streams, ContentType=WindowsRuntime]
                                    $reader = New-Object $readerType ($stream.GetInputStreamAt(0))
                                    try {
                                        $loaded = Await-Result ($reader.LoadAsync([uint32]$stream.Size)) ([uint32])
                                        $bytes = New-Object byte[] $loaded
                                        $reader.ReadBytes($bytes)
                                        $art = [Convert]::ToBase64String($bytes)
                                    } finally { $reader.Dispose() }
                                }
                            } finally { $stream.Dispose() }
                        }
                        $lastArtKey = $artKey
                    } catch { $lastArtKey = '' }
                }
                $timeline = $session.GetTimelineProperties()
                $info = $session.GetPlaybackInfo()
                $playing = $info.PlaybackStatus.ToString() -eq 'Playing'
                $rate = 1.0
                if ($null -ne $info.PlaybackRate -and $info.PlaybackRate -gt 0) { $rate = [double]$info.PlaybackRate }
                $position = ($timeline.Position - $timeline.StartTime).TotalMilliseconds
                $duration = ($timeline.EndTime - $timeline.StartTime).TotalMilliseconds
                $age = [Math]::Max(0,($now - $timeline.LastUpdatedTime).TotalMilliseconds)
                $out = @{ ready=$true; artwork=$art; title=$props.Title; artist=$props.Artist; album=$props.AlbumTitle; position=[Math]::Max(0,$position); duration=[Math]::Max(0,$duration); playing=$playing; rate=$rate; updatedAt=$timeline.LastUpdatedTime.ToUnixTimeMilliseconds(); anchorAge=$age }
                [Console]::WriteLine(($out | ConvertTo-Json -Compress))
            }
        } catch { [Console]::WriteLine('{"ready":false,"error":"media"}') }
        Start-Sleep -Milliseconds 250
    }
} catch { [Console]::WriteLine('{"ready":false,"error":"windows"}'); exit 1 }
