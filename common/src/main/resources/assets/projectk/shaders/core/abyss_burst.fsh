#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GameTime;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in vec4 vertexColor;
in vec2 texCoord;

out vec4 fragColor;

void main() {
    float angle = GameTime * 1000.0;
    float s = sin(angle);
    float c = cos(angle);
    vec2 pos = texCoord - 0.5;
    vec2 rotatedPos = vec2(pos.x * c - pos.y * s, pos.x * s + pos.y * c) + 0.5;
    
    vec4 texColor = texture(Sampler0, rotatedPos);
    if (texColor.a < 0.1) discard;

    vec3 vibrantColor = vertexColor.rgb * 1.5;

    vec4 baseColor = vec4(vibrantColor, vertexColor.a) * texColor * ColorModulator;

    float fogFactor = clamp((gl_FragCoord.z - FogStart) / max(FogEnd - FogStart, 0.0001), 0.0, 1.0);
    vec3 fogged = mix(baseColor.rgb, FogColor.rgb, fogFactor);
    fragColor = vec4(fogged, baseColor.a);
}
