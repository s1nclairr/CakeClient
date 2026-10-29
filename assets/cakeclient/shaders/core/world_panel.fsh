#version 330
uniform sampler2D CardSampler;
layout(std140) uniform CardData { mat4 ViewProjection; vec4 Parameters; vec4 Tint; vec4 HoverRect; };
in vec2 texCoord;
out vec4 fragColor;
void main(){
 vec4 c=texture(CardSampler,texCoord);
 if(Parameters.z>.5){
  float fill=1.0-smoothstep(Parameters.y-.006,Parameters.y+.006,texCoord.x);
  float core=smoothstep(.80,.97,min(c.r,min(c.g,c.b)));
  vec3 highlight=mix(vec3(.96,.90,1.),vec3(.76,.32,1.),fill);
  c.rgb=mix(c.rgb,highlight,core);
 }
 if(Parameters.z<.5&&HoverRect.z>0.){
  vec2 halfSize=HoverRect.zw*.5,center=HoverRect.xy+halfSize;
  if(all(lessThanEqual(abs(texCoord*vec2(1920.,1080.)-center),halfSize+vec2(2.)))){
  float radius=min(20.,min(halfSize.x,halfSize.y));
  vec2 q=abs(texCoord*vec2(1920.,1080.)-center)-halfSize+radius;
  float distance=length(max(q,0.))+min(max(q.x,q.y),0.)-radius;
  float inside=1.-smoothstep(-1.,1.,distance),edge=1.-smoothstep(.5,2.,abs(distance));
  c.rgb+=vec3(.07,.045,.10)*inside+vec3(.14,.09,.22)*edge;
  }
 }
 c.a*=Parameters.x;
 if(c.a<.005)discard;
 fragColor=c;
}
