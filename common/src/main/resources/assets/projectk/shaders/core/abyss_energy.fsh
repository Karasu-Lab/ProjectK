#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 texColor = texture(Sampler0, texCoord);
    if (texColor.a < 0.1) discard;

    vec3 baseColor = vec3(0.5, 0.0, 1.0);
    
    fragColor = vec4(baseColor, 0.7) * vertexColor * ColorModulator;
}
