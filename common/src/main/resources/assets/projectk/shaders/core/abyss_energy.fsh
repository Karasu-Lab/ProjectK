#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GameTime;

in vec4 vertexColor;
in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 texColor = texture(Sampler0, texCoord);
    if (texColor.a < 0.1) discard;

    // Pulse effect
    float pulse = sin(GameTime * 5000.0) * 0.2 + 0.8;
    
    // Mix the vibrant energy color with the texture's luminosity
    vec3 vibrantColor = vertexColor.rgb * pulse;
    
    fragColor = vec4(vibrantColor, vertexColor.a) * texColor * ColorModulator;
}
