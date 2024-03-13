
function latLngToWebMercator(longitude, latitude) {
    const earthRad = 6378137.0;
    const x = (longitude * Math.PI / 180) * earthRad;
    const a = (latitude * Math.PI / 180);
    const y = (earthRad / 2) * Math.log((1.0 + Math.sin(a)) / (1.0 - Math.sin(a)));
    return [x, y];
}

export function latLngToThreejs(_lat, _lng, _coordinate) {

    let mercatorOrigin = latLngToWebMercator(_coordinate.lng, _coordinate.lat);
    let offsetX = -mercatorOrigin[0];
    let offsetZ = -mercatorOrigin[1];

    const mercatorCoordinates = latLngToWebMercator(_lng, _lat);
    const x = (mercatorCoordinates[0] + offsetX) / 1.0870778616;
    const z = (mercatorCoordinates[1] + offsetZ) / 1.0870778616;

    return { x:x/10, y: 0, z:z/10 };

}