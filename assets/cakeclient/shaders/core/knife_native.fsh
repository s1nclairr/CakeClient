#version 330
uniform sampler2D AlbedoSampler;uniform sampler2D SurfaceSampler;

in vec3 position;in vec3 normal;in vec2 uv;out vec4 fragColor;
void main(){
 vec3 base=pow(texture(AlbedoSampler,uv).rgb,vec3(2.2));vec3 orm=texture(SurfaceSampler,uv).rgb;
 float rough=clamp(orm.r,.12,.98),metal=orm.g,ao=orm.b;vec3 n=normalize(normal),v=normalize(-position);if(dot(n,v)<0.)n=-n;
 if(CAKE_FINISH==1)base=mix(base,vec3(.22,.035,.48),metal*.6);else if(CAKE_FINISH==2)base*=mix(1.,.25,metal);
 vec3 l=normalize(vec3(-.4,.8,.7)),h=normalize(l+v);float nl=max(dot(n,l),0.),nv=max(dot(n,v),.001),nh=max(dot(n,h),0.),vh=max(dot(v,h),0.);
 float a=rough*rough,a2=a*a;float d=a2/(3.14159*pow(nh*nh*(a2-1.)+1.,2.)+.00001);float k=pow(rough+1.,2.)/8.;float g=nl/(nl*(1.-k)+k)*nv/(nv*(1.-k)+k);
 vec3 f0=mix(vec3(.04),base,metal),f=f0+(1.-f0)*pow(1.-vh,5.);vec3 spec=d*g*f/(4.*max(nl*nv,.001));
 vec3 reflected=reflect(-v,n);vec3 env=mix(vec3(.08,.09,.13),vec3(.65,.75,.96),smoothstep(-.3,.8,reflected.y));env+=vec3(.9,.74,.5)*pow(max(0.,dot(reflected,normalize(vec3(-.4,.8,.7)))),mix(5.,70.,1.-rough));
 vec3 color=base*(.38*ao+nl*(1.-metal)*.95)+spec*nl*.9+env*f0*(.45+metal*.7)*ao;
 color=color/(1.+color*.35);fragColor=vec4(pow(max(color,0.),vec3(1./2.2)),1.);
}
