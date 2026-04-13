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
    vec4 texColor = texture(Sampler0, texCoord);
    if (texColor.a < 0.1) discard;

    float pulse = sin(GameTime * 5000.0) * 0.2 + 0.8;
    
    vec3 vibrantColor = vertexColor.rgb * pulse;

    vec4 baseColor = vec4(vibrantColor, vertexColor.a) * texColor * ColorModulator;

    float fogFactor = clamp((gl_FragCoord.z - FogStart) / max(FogEnd - FogStart, 0.0001), 0.0, 1.0);
    vec3 fogged = mix(baseColor.rgb, FogColor.rgb, fogFactor);
    fragColor = vec4(fogged, baseColor.a);
}
