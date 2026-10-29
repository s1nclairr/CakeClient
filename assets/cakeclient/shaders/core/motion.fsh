#version 330
uniform sampler2D CurrentSampler;
uniform sampler2D HistorySampler;
layout(std140) uniform CakeConfig { vec4 Temporal; };
in vec2 texCoord;
out vec4 fragColor;
void main() {
    vec3 current = texture(CurrentSampler, texCoord).rgb;
    vec3 history = texture(HistorySampler, texCoord).rgb;
    fragColor = vec4(mix(current, history, clamp(Temporal.x, 0.0, 0.99)), 1.0);
}
