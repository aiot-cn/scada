
function getCellData(cell){
    var o = {};
    var v = cell.value;
    if (typeof(v) == 'object') {
        var attrs = v.attributes;
        for(var i=0;i<attrs.length;i++){
            var attr = attrs[i];
            o[attr.name] = attr.value;
        }
    }
    return o;
}