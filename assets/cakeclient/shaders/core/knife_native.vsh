#version 330
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
in vec3 Position;in vec2 UV0;in vec4 Color;in vec3 Normal;
out vec3 position;out vec3 normal;out vec2 uv;
void main(){vec4 p=ModelViewMat*vec4(Position,1.);position=p.xyz;normal=normalize(mat3(ModelViewMat)*Normal);uv=UV0;gl_Position=ProjMat*p;}
