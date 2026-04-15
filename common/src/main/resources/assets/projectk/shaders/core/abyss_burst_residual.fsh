#version 150
 
uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
 
in vec2 texCoord0;
in vec4 vertexColor;
 
out vec4 fragColor;
 
void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    
    vec4 finalColor = texColor * vertexColor * ColorModulator;
    
    if (finalColor.a < 0.1) {
        discard;
    }
    
    fragColor = finalColor;
}
