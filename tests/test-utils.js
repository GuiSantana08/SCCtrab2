function printStatus(requestParams, response, context, ee, next) {
    console.log(`${response.body}: ${response.statusCode}`);
    return next();
}

function delay(requestParams, context, ee, next) {
    setTimeout(function () {
        next();
    }, 5000); // delay of 5 seconds
}

module.exports = {
    printStatus: printStatus,
    delay: delay,
}