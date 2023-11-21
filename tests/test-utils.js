module.exports = {
    printStatus,
    delay,
    uploadImageBody,
}

var images = []

const fs = require('fs')

function printStatus(requestParams, response, context, ee, next) {
    console.log(`${response.body}: ${response.statusCode}`);
    return next();
}

function delay(requestParams, context, ee, next) {
    setTimeout(function () {
        next();
    }, 5000); // delay of 5 seconds
}


// Loads data about images from disk
function loadData() {
    var i
    var basefile
    if (fs.existsSync('/images'))
        basefile = '/images/house.'
    else
        basefile = 'images/house.'
    for (i = 1; i <= 50; i++) {
        var img = fs.readFileSync(basefile + i + '.jpg')
        images.push(img)
    }
}

loadData();

// Auxiliary function to select an element from an array
Array.prototype.sample = function () {
    return this[Math.floor(Math.random() * this.length)]
}

/**
 * Sets the body to an image, when using images.
 */
function uploadImageBody(requestParams, context, ee, next) {
    requestParams.body = images.sample()
    return next()
}
