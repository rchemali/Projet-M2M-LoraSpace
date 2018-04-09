var express = require('express');
var router = express.Router();
var app = express();
var dialog = require('dialog');

/* GET home page. */

var Client = require('node-rest-client').Client
var client = new Client();


router.get('/', function (req, res, next) {

    var mqtt = require('mqtt');
    var clientMqtt = mqtt.connect('mqtt://broker.hivemq.com');

    clientMqtt.on('connect', function () {
        clientMqtt.subscribe('m2m-loraspace/data/gps');
    });

    clientMqtt.on('message', function (topic, message) {

        var tabGps = message.toString().split("#");

        var latitude = tabGps[0].trim();
        var longitude = tabGps[1].trim();
        var altitude = tabGps[2].preplace(/[^\w\s]/gi, '')

        console.log(tabGps[0]);
        console.log(tabGps[1]);
        console.log(tabGps[2]);

        id = req.query.id;
        azimuthVal = req.query.azimuth;

        var path = "https://www.n2yo.com/rest/v1/satellite/positions/" +id+ "/"+latitude+"/"+longitude+"/"+altitude+"/1&apiKey=X89TH9-A5E6R3-BKEVGH-3R4W";
        console.log(path);

        client.get(path, function (data, response) {
            //   res.render('detail', {val:data['positions'] , name : ename});
            res.status(200).send(data['positions']);

            console.log(azimuthVal);

            if (azimuthVal!=null){
            sendData(data['positions']);
            }

        });


        clientMqtt.end();
    });
});

module.exports = router;

function sendData(data) {
    var mqtt = require('mqtt');
    var client = mqtt.connect('http://broker.hivemq.com');
    client.on('connect', function () {
        console.log(JSON.stringify(data))
        client.publish('m2m-loraspace/config/servos', JSON.stringify(data[0]))
    })
}



