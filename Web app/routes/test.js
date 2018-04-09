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

    nom = "Sat"
    id = req.query.id;
    nom = req.query.name;
    azimuthVal = req.query.azimuth;

    var data = {
        "azimuth"  :  "180",
        "elevation"   :  "0"
    }

    var tab = []

    tab.push(data)
    //   res.render('detail', {val:data['positions'] , name : ename});
    res.status(200).send(tab);

    sendData(tab, nom);

    if (azimuthVal != undefined) {

        sendData(data['positions'], nom);
    }
    //if (azimuthVal!=null){
    //    sendData(data['positions']);
    //}o


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


