#version 330
uniform sampler2D InsectSampler;
in vec2 texCoord;in vec4 tint;out vec4 fragColor;
void main(){vec4 c=texture(InsectSampler,texCoord)*tint;if(c.a<.03)discard;fragColor=c;}
