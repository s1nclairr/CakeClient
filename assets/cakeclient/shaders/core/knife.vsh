#version 330
in vec3 Position;in vec2 UV0;in vec4 Color;in vec3 Normal;
layout(std140) uniform ViewModelData { mat4 Projection;mat4 Model;vec4 Material; };
out vec3 position;out vec3 normal;out vec2 uv;
void main(){vec4 p=Model*vec4(Position,1.);position=p.xyz;normal=normalize(mat3(Model)*Normal);uv=UV0;gl_Position=Projection*p;}
