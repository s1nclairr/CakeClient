#version 330
in vec3 Position;
in vec2 UV0;
in vec4 Color;
layout(std140) uniform CardData { mat4 ViewProjection; vec4 Parameters; vec4 Tint; vec4 HoverRect; };
out vec2 texCoord;
void main(){gl_Position=ViewProjection*vec4(Position,1.0);texCoord=UV0;}
