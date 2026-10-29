#version 330
#moj_import <minecraft:fog.glsl>
uniform sampler2D AvatarSampler;in vec2 uv;in vec3 position;in vec3 normal;in vec4 color;out vec4 fragColor;
void main(){vec3 n=normalize(normal);if(!gl_FrontFacing)n=-n;vec3 light=normalize(vec3(-.4,.8,.7)),view=normalize(-position);float diffuse=max(0.,dot(n,light));float spec=pow(max(0.,dot(n,normalize(light+view))),32.);vec4 albedo=texture(AvatarSampler,uv)*color;if(albedo.a<.1)discard;vec3 rgb=albedo.rgb*(.47+.53*diffuse)+vec3(.10)*spec;fragColor=apply_fog(vec4(rgb,albedo.a),length(position),max(length(position.xz),abs(position.y)),FogEnvironmentalStart,FogEnvironmentalEnd,FogRenderDistanceStart,FogRenderDistanceEnd,FogColor);}
