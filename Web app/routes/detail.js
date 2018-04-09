var express = require('express');
var router = express.Router();

/* GET home page. */

var Client = require('node-rest-client').Client

var client = new Client();


client.on('message', function (topic, message) {
    console.log(topic);
    console.log(message.toString());
    client.end();
});

router.get('/', function (req, res, next) {

    id = req.query.id;
    nom = req.query.name;
    //azimuthVal = req.query.azimuth;

    client.get("https://www.n2yo.com/rest/v1/satellite/positions/" + id + "/45/5/200/1&apiKey=X89TH9-A5E6R3-BKEVGH-3R4W", function (data, response) {

           res.render('detail', {val:data['positions'] , name : nom});
       // res.status(200).send(data['positions']);

        //sendData(data['positions'],nom);

       // if (azimuthVal != undefined) {

         //   sendData(data['positions'], nom);
        //}
        //if (azimuthVal!=null){
        //    sendData(data['positions']);
        //}

    });

});

module.exports = router;

function sendData(data, name) {
    var mqtt = require('mqtt');
    var client = mqtt.connect('http://broker.hivemq.com');
    client.on('connect', function () {
        console.log(JSON.stringify(data))
        client.publish('m2m-loraspace/data/gps/name', name);
        client.publish('m2m-loraspace/config/servos', JSON.stringify(data[0]))
    })
}


