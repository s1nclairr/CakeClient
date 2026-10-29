$code=@'
using System;using System.Runtime.InteropServices;using System.Threading;using System.Globalization;
[ComImport,Guid("BCDE0395-E52F-467C-8E3D-C4579291692E")]class Enumerator{}
[ComImport,Guid("A95664D2-9614-4F35-A746-DE8DB63617E6"),InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]interface IEnumerator{
 [PreserveSig]int EnumAudioEndpoints(int flow,uint mask,out IntPtr devices);[PreserveSig]int GetDefaultAudioEndpoint(int flow,int role,out IDevice device);int GetDevice([MarshalAs(UnmanagedType.LPWStr)]string id,out IDevice device);int Register(IntPtr client);int Unregister(IntPtr client);
}
[ComImport,Guid("D666063F-1587-4E43-81F1-B948E807363F"),InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]interface IDevice{
 [PreserveSig]int Activate(ref Guid id,uint context,IntPtr activation,[MarshalAs(UnmanagedType.IUnknown)]out object value);int OpenPropertyStore(int mode,out IntPtr store);int GetId(out IntPtr id);int GetState(out uint state);
}
[ComImport,Guid("C02216F6-8C67-4B5B-9D00-D008E73E0064"),InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]interface IMeter{
 [PreserveSig]int GetPeakValue(out float peak);[PreserveSig]int GetMeteringChannelCount(out uint count);[PreserveSig]int GetChannelsPeakValues(uint count,[Out,MarshalAs(UnmanagedType.LPArray,SizeParamIndex=0)]float[] values);[PreserveSig]int QueryHardwareSupport(out uint mask);
}
public static class CakeMeter{public static void Run(){try{var enumerator=(IEnumerator)new Enumerator();IDevice device;Marshal.ThrowExceptionForHR(enumerator.GetDefaultAudioEndpoint(0,1,out device));Guid id=new Guid("C02216F6-8C67-4B5B-9D00-D008E73E0064");object value;Marshal.ThrowExceptionForHR(device.Activate(ref id,23,IntPtr.Zero,out value));var meter=(IMeter)value;while(true){float peak;Marshal.ThrowExceptionForHR(meter.GetPeakValue(out peak));Console.WriteLine(peak.ToString("F5",CultureInfo.InvariantCulture));Thread.Sleep(33);}}catch(Exception){Console.WriteLine("unavailable");}}}
'@
Add-Type -TypeDefinition $code
[CakeMeter]::Run()
