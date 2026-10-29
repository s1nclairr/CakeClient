#version 330
uniform sampler2D SceneSampler;uniform sampler2D DepthSampler;
layout(std140) uniform AtmosphereData { vec4 ResolutionBloomAO;vec4 Grade; };
in vec2 texCoord;out vec4 fragColor;
float depthAt(vec2 uv){return .05/max(.000001,1.-texture(DepthSampler,uv).r);}
void main(){
 vec2 pixel=1./ResolutionBloomAO.xy;vec4 scene=texture(SceneSampler,texCoord);vec3 color=scene.rgb;
 if(ResolutionBloomAO.z>.001){
  vec3 bloom=vec3(0.);float weight=0.;
  for(int i=0;i<12;i++){float angle=float(i)*2.399963;float radius=3.+float(i)*1.9;
   vec3 sampleColor=texture(SceneSampler,texCoord+vec2(cos(angle),sin(angle))*pixel*radius).rgb;
   float bright=max(max(sampleColor.r,sampleColor.g),sampleColor.b);float w=1.-float(i)/16.;
   bloom+=sampleColor*smoothstep(.68,.98,bright)*w;weight+=w;
  }color+=bloom/max(weight,1.)*ResolutionBloomAO.z*.48;
 }
 float rawDepth=texture(DepthSampler,texCoord).r;
 if(ResolutionBloomAO.w>.001&&rawDepth<.99999){
  float center=depthAt(texCoord),ao=0.;
  for(int i=0;i<8;i++){float a=float(i)*.785398;vec2 offset=vec2(cos(a),sin(a))*pixel*(3.+float(i%3)*2.);
   float other=depthAt(texCoord+offset),delta=center-other;
   ao+=smoothstep(.025,.18,delta)*(1.-smoothstep(.3,1.6,abs(delta)));
  }color*=1.-ao*.075*ResolutionBloomAO.w;
 }
 color*=exp2((Grade.x-.5)*2.);
 if(Grade.y<.5){color=mix(vec3(dot(color,vec3(.2126,.7152,.0722))),color,1.04);}
 else if(Grade.y<1.5){color=pow(max(color,vec3(0.)),vec3(.96,1.,1.06));color=mix(vec3(dot(color,vec3(.2126,.7152,.0722))),color,.88);}
 else if(Grade.y<2.5){color*=vec3(1.07,1.015,.92);color=pow(max(color,vec3(0.)),vec3(.95));}
 else {color*=vec3(.88,.96,1.12);color=pow(max(color,vec3(0.)),vec3(.94));}
 float edge=dot(texCoord-.5,texCoord-.5);color*=1.-smoothstep(.08,.48,edge)*Grade.z*.35;
 color=color/(1.+max(color-1.,0.)*.35);fragColor=vec4(color,scene.a);
}
