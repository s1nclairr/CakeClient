#version 330
in vec3 Position;in vec2 UV0;in vec4 Color;
layout(std140) uniform InsectData { mat4 ViewProjection; };
out vec2 texCoord;out vec4 tint;
void main(){gl_Position=ViewProjection*vec4(Position,1.);texCoord=UV0;tint=Color;}
