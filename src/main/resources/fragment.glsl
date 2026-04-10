#version 150
uniform sampler2D uTexture;
uniform vec2 uDirection;
uniform float uRadius;

uniform vec2 uLocation;
uniform vec2 uSize;
uniform vec4 uRounding; // tr, br, tl, bl
uniform int uPass;

in vec2 texCoord;
out vec4 fragColor;

float gaussian(float x, float sigma) {
    return exp(-(x * x) / (2.0 * sigma * sigma));
}

float sdRoundedBox(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x  = (p.y > 0.0) ? r.x  : r.y;
    vec2 q = abs(p) - b + r.x;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r.x;
}

void main() {
    vec2 texSize = textureSize(uTexture, 0);
    
    if (uPass == 1) {
        vec2 pixelPos = texCoord * texSize;
        vec2 glLocation = vec2(uLocation.x, texSize.y - uLocation.y - uSize.y);
        vec2 center = glLocation + uSize * 0.5;
        float dist = sdRoundedBox(pixelPos - center, uSize * 0.5, uRounding);
        if (dist > 0.0) discard;
    }

    float sigma = uRadius / 2.0;
    vec4 color = texture(uTexture, texCoord) * gaussian(0.0, sigma);
    float totalWeight = gaussian(0.0, sigma);
    vec2 step = uDirection / texSize;
    
    for (float i = 1.0; i <= uRadius; i++) {
        float weight = gaussian(i, sigma);
        color += texture(uTexture, texCoord + i * step) * weight;
        color += texture(uTexture, texCoord - i * step) * weight;
        totalWeight += 2.0 * weight;
    }
    
    fragColor = color / totalWeight;
}
