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
        clientMqtt.publish('m2m-loraspace/data/gps',"value");
    });

    clientMqtt.on('message', function (topic, message) {

        var tabGps = message.toString().split("#");

        //var latitude = tabGps[0].trim();
        //var longitude = tabGps[1].trim();
        //var altitude = tabGps[2].replace(/[^\w\s]/gi, '')
        //var altitude = tabGps[2].replace('\t','');
        //.replace("xxxx", '')
        var latitude = 45;
        var longitude = 5;
        var altitude = 200;
        //var ongle = req.query.angle;

        var ongle =90;
        console.log(tabGps[0]);
        console.log(tabGps[1]);
        console.log(tabGps[2]);

        var path = "https://www.n2yo.com/rest/v1/satellite/above/" + latitude + "/" + longitude + "/" + altitude + "/"+ongle+"/18&apiKey=X89TH9-A5E6R3-BKEVGH-3R4W";
        console.log(path);

        client.get(path, function (data, response) {

            res.render('home', {list:data['above']});

  //          console.log(path);
//            res.status(200).send(data["above"]);

        });

        clientMqtt.end();
    });
});

module.exports = router;



