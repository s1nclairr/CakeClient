#version 330
uniform sampler2D SceneSampler;uniform sampler2D DepthSampler;
layout(std140) uniform SkyData { mat4 InverseViewProjection; vec4 SkyColor; vec4 Parameters; };
in vec2 texCoord;out vec4 fragColor;
float hash3(vec3 p){return fract(sin(dot(p,vec3(127.1,311.7,74.7)))*43758.5453);}
float mist(vec3 p){float a=.55,v=0.;for(int i=0;i<3;i++){v+=a*(.5+.5*sin(p.x+sin(p.z))*cos(p.y+sin(p.x*.8)));p=p*2.08+vec3(4.1,7.3,2.5);a*=.5;}return v;}
float stars(vec2 p,float seed){
 vec2 cell=floor(p),f=fract(p);float chance=hash3(vec3(cell,seed));vec2 center=vec2(hash3(vec3(cell,seed+3.)),hash3(vec3(cell,seed+9.)))*.7+.15;
 float r=length(f-center);float dotStar=exp(-r*r*140.)*step(1.-Parameters.w*.055,chance);
 float bright=step(.9975,chance)*exp(-r*r*28.);return dotStar+bright*.7;
}
mat3 ry(float a){float c=cos(a),s=sin(a);return mat3(c,0,-s,0,1,0,s,0,c);}
void main(){
 vec4 scene=texture(SceneSampler,texCoord);if(texture(DepthSampler,texCoord).r<.999999){fragColor=scene;return;}
 vec4 p=InverseViewProjection*vec4(texCoord*2.-1.,1.,1.);vec3 ray=normalize(p.xyz/max(abs(p.w),.00001));
 float time=Parameters.y*(.12+Parameters.z*.85);vec3 d=ry(time*(Parameters.x>4.5?.006:.065))*ray;
 vec3 tone=SkyColor.rgb,blue=vec3(.12,.25,.78),pink=vec3(.9,.26,.68);
 float fog=mist(d*3.+vec3(time*.10,0.,-time*.07));
 vec3 sky=mix(tone*.065,blue*.22,.5+.5*d.y)+tone*pow(fog,3.)*.65;
 float mode=Parameters.x,starMask=1.;
 if(mode<.5){sky=mix(tone*.28,tone*.85,.5+.5*d.y);sky+=tone*mist(d*4.+time*.12)*.12;}
 else if(mode<1.5){
  vec3 axis=normalize(vec3(-.22,.32,.92));float radius=acos(clamp(dot(d,axis),-1.,1.));
  float ring=exp(-pow((radius-.42-.06*sin(time*.75))*9.,2.));
  float ring2=exp(-pow((radius-.83-.1*sin(time*.5))*6.,2.));
  float core=pow(max(0.,dot(d,axis)),72.);float turbulence=mist(d*12.+vec3(time*.5,time*.2,0.));
  sky+=tone*(ring*1.3+ring2*.55)*(.5+turbulence)+vec3(.85,.76,1.)*core*2.5;
  sky+=pink*pow(fog,4.)*.7;
 }else if(mode<2.5){
  vec3 axis=normalize(vec3(.2,.82,.54));float band=exp(-pow(dot(d,axis)*3.9,2.));
  float cloud=mist(d*7.+vec3(time*.16,-time*.08,time*.1));
  float dust=mist(d*19.+vec3(-time*.13,0,time*.11));
  sky+=mix(tone*1.5,pink,.35+.35*sin(time*.15+d.x*4.))*band*(.23+cloud*1.65);
  sky*=1.-band*pow(dust,4.)*.6;sky+=blue*pow(fog,3.)*.9;
  vec3 coreAxis=normalize(vec3(.25,.24,.94)),side=normalize(cross(coreAxis,vec3(0,1,0))),vertical=cross(side,coreAxis);
  float alignment=dot(d,coreAxis),radius=acos(clamp(alignment,-1.,1.));float angle=atan(dot(d,vertical),dot(d,side));
  float arms=pow(.5+.5*cos(angle*3.+radius*17.-time*.55),5.)*exp(-radius*2.8)*smoothstep(0.,.12,radius);
  sky+=mix(tone,pink,.5)*arms*1.1+vec3(1.,.82,.67)*pow(max(0.,alignment),150.)*2.5;
 }else if(mode<3.5){
  float wave=sin(dot(d,normalize(vec3(.8,.25,.4)))*22.+fog*5.-time*1.5);
  float wave2=sin(dot(d,normalize(vec3(-.4,.75,.6)))*15.+sin(time*.4+d.z*7.)*2.+time);
  sky+=mix(tone,pink,.5+.5*sin(d.y*5.+time*.3))*pow(.5+.5*wave,6.)*.95;
  sky+=blue*pow(.5+.5*wave2,9.)*.7;
 }else if(mode<4.5){
  float stripes=pow(.5+.5*sin(d.x*25.+sin(d.z*8.+time*.5)*3.+fog*9.-time),5.);
  float ribbon=exp(-pow((d.y-.32-.14*sin(d.z*6.+time*.35))*5.,2.));
  sky+=mix(tone,vec3(.12,1.,.56),.55)*stripes*ribbon*1.9;
  sky+=tone*pow(fog,3.)*.9;
 }
 else if(mode<5.5){
  // Direction-space lens and accretion disk: no screen-space edges or longitude seam.
  vec3 axis=normalize(vec3(.18,.42,.89)),horizontal=normalize(cross(vec3(0,1,0),axis)),vertical=cross(axis,horizontal);
  vec2 uv=vec2(dot(d,horizontal),dot(d,vertical))/max(.15,dot(d,axis));float front=smoothstep(.05,.4,dot(d,axis));
  float c=cos(.25),s=sin(.25);uv=mat2(c,-s,s,c)*uv;
  float radius=length(uv),core=.18;float disk=length(vec2(uv.x,uv.y*4.8));float angle=atan(uv.y*4.8,uv.x);
  float turbulence=.68+.32*sin(disk*125.-time*3.2+sin(angle*7.+time)*.9);
  float accretion=exp(-pow((disk-.37)*15.,2.))*(.72+.28*sin(angle*3.-time))*turbulence;
  float lens=exp(-pow((radius-core*1.14)*60.,2.));
  float backArc=exp(-pow((length(vec2(uv.x,uv.y*.92))-.29)*35.,2.))*smoothstep(-.03,.05,uv.y)*.7;
  vec3 diskColor=mix(vec3(1.,.52,.82),vec3(.5,.16,1.),smoothstep(.25,.5,disk));
  sky=sky*.28+(diskColor*accretion*5.+vec3(.92,.72,1.)*(lens*3.+backArc*2.))*front;
  float hole=(1.-smoothstep(core-.008,core+.006,radius))*front;sky*=1.-hole;starMask=1.-hole;
 }
 else if(mode<6.5){
  vec3 sunAxis=normalize(vec3(.16,.43,.89));float r=acos(clamp(dot(d,sunAxis),-1.,1.));
  float corona=exp(-r*9.)*(.8+.2*sin(atan(d.y,d.x)*21.+time*.7));
  float rim=exp(-pow((r-.175)*90.,2.));sky=sky*.3+vec3(1.,.58,.18)*(corona*.8+rim*3.);
  float eclipse=1.-smoothstep(.157,.166,r);sky*=1.-eclipse;starMask=1.-eclipse;
 }else if(mode<7.5){
  float neb=mist(d*5.+vec3(time*.08,-time*.03,time*.05));float veins=mist(d*17.+neb*3.+time*.07);
  vec3 gas=mix(vec3(.06,.52,.7),vec3(.7,.1,.72),smoothstep(.3,.7,neb));
  sky=sky*.4+gas*pow(neb,2.)*1.8+vec3(1.,.35,.11)*pow(veins,8.)*2.;
 }else if(mode<8.5){
  sky=sky*.5+blue*pow(fog,4.)*.4;
  for(int i=0;i<5;i++){
   float seed=float(i),phase=fract(time*(.075+seed*.013)+seed*.217);
   vec3 a=normalize(vec3(-.9+seed*.44,.65,.7)),b=normalize(vec3(-.35+seed*.35,.1,1.));
   vec3 head=normalize(mix(a,b,phase));float h=pow(max(0.,dot(d,head)),18000.);
   float tail=0.;for(int j=1;j<5;j++){vec3 t=normalize(mix(a,b,max(0.,phase-float(j)*.013)));tail+=pow(max(0.,dot(d,t)),13000.)*(1.-float(j)*.19);}
   sky+=vec3(.65,.8,1.)*(h*3.+tail)*smoothstep(0.,.08,phase)*(1.-smoothstep(.8,1.,phase));
  }
 }else{
  float curtain=sin(d.x*37.+sin(d.z*11.+time*.3)*3.+time*.8);
  float envelope=exp(-pow((d.y-.43-.16*sin(d.z*6.-time*.25))*3.5,2.));
  vec3 polar=mix(vec3(.04,.85,.52),vec3(.47,.22,1.),.5+.5*sin(d.x*5.+time*.17));
  sky=sky*.4+polar*pow(.5+.5*curtain,6.)*envelope*2.+vec3(.4,.8,1.)*pow(fog,7.);
 }
 // A three-dimensional star field has no longitude seam, nor an abrupt horizon cutoff.
 vec3 weights=pow(abs(d),vec3(8.));weights/=max(.00001,weights.x+weights.y+weights.z);
 float star=stars(d.yz*200.,1.)*weights.x+stars(d.xz*200.,17.)*weights.y+stars(d.xy*200.,29.)*weights.z;
 sky+=vec3(.8,.86,1.)*star*1.5*starMask;
 sky=1.-exp(-max(sky,vec3(0.))*1.35);
 // Cover every background direction, including the horizon and lower hemisphere.
 // The depth guard above preserves terrain, entities and held items.
 fragColor=vec4(mix(scene.rgb,sky,SkyColor.a),scene.a);
}
