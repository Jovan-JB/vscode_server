var express = require ('express');
var bodyparser = require('body-parser');
var fs = require('fs');
var mysql = require('mysql');
var app = express();

app.use(bodyparser.json());
const {json} = require('body-parser');
const { connect } = require('http2');

const conn = mysql.createConnection({
    host : 'localhost',
    user : 'root',
    password : '',
    database : 'pat_sewamobil'
});

conn.connect(function(err){
    if (err) throw err;
    console.log("MySQL connected...");
});


//login admin toko
app.post('/loginadmin', function(req, res) {
    console.log("POST request /loginadmin");
    let username = {user: req.body.username};
    json.getString
    console.log("POST request data ="+JSON.stringify(username.user));
    let password = {pass: req.body.password};
    console.log("POST request data ="+JSON.stringify(password.pass));
    let sql = "SELECT id_toko, nama FROM admin WHERE nama='"+username.user+"' AND password = '"+password.pass+"'";
    console.log(sql)
    let query = conn.query(sql, (err, result) => {
        console.log(JSON.stringify(
            {"status" : 200, "error" : null, "response" : result}
        ));
        if(result != "") {
            // res.send(result)
            res.send("Login Berhasil")
        }
        else {
            res.send("Login Gagal")}
    });
});

//register akun user
app.post('/register', function(req, res) {
    console.log('POST request /register');
    let username = {user: req.body.username};
    json.getString
    console.log("POST request data ="+JSON.stringify(username.user));

    let password = {pass: req.body.password};
    console.log("POST request data ="+JSON.stringify(password.pass));

    let email = {mail: req.body.email};
    console.log("POST request data ="+JSON.stringify(email.mail));

    let check = "SELECT user_id FROM users WHERE user_name ='"+username.user+"'";

    let checker = conn.query(check, (err, checkresult)=>{
        console.log(JSON.stringify(
            {
                "status" : 200,
                "error" : null,
                "response" : checkresult
            }
        ));
        console.log(checkresult);
        if (checkresult == ""){
            let sql = "INSERT INTO users (user_name, password, email) VALUES ('"+username.user+"','"+password.pass+"','"+email.mail+"')";
            let query = conn.query(sql, (err, result) =>{
                console.log(JSON.stringify(
                    {
                        "status" : 200,
                        "error" : null,
                        "response" : result
                    }
                ));
                conn.query(check, (err, checkresult) => {
                    console.log(JSON.stringify(
                        {
                            "status" : 200,
                            "error" : null,
                            "response" : checkresult
                        }
                    ));
                });
                // res.send(checkresult);
                res.send("Pendaftaran Berhasil")
            });
        }
        else {
            // res.send(checkresult);
            res.send("Pendaftaran Gagal")
        }
    })
});

//login user
app.post('/loginuser', function(req, res) {
    console.log("POST request /loginuser");
    let username = {user: req.body.username};
    let password = {pass: req.body.password};
  
    let sql = "SELECT user_id, user_name FROM users WHERE user_name='"+username.user+"' AND password = '"+password.pass+"'";
    console.log(sql);
  
    let query = conn.query(sql, (err, result) => {
      if (err) {
        console.log(err);
        res.status(500).json({ status: 500, error: 'Internal Server Error', response: null });
      } else {
        if (result.length > 0) {
          res.status(200).json({ status: 200, error: null, response: 'Login Berhasil' });
        } else {
          res.status(401).json({ status: 401, error: 'Unauthorized', response: 'Login Gagal' });
        }
      }
    });
  });

//list kendaraan
app.get('/listkendaraan', function(req, res) {
    console.log('Menerima GET request /listkendaraan');
    let sql = "SELECT * FROM list_mobil";
    let query = conn.query(sql, function(err, result){
        if (err) throw err;
        res.send(JSON.stringify({
            "status" : 200,
            "error" : null,
            "response" : result
        }));
    });
});

//list booking
app.get('/listbooking', function(req, res) {
    console.log('Menerima GET request /listbooking');
    let sql = "SELECT * FROM booking";
    let query = conn.query(sql, function(err, result) {
        if (err) throw err;

        console.log(JSON.stringify({
            "status": 200,
            "error": null,
            "response": result
        }));

        res.send(JSON.stringify({
            "status": 200,
            "error": null,
            "response": result
        }));
    });
});

//edit list kendaraan
app.put('/editkendaraan', function(req, res) {
    let jumlah = {Jumlah: req.body.jumlah};
    let harga = {Harga: req.body.harga};
    let jenis = {Jenis: req.body.jenis};
    console.log(jumlah)
    console.log(harga)
    console.log(jenis)
    let sql = "UPDATE list_mobil SET jumlah_mobil='"+jumlah.Jumlah+"', harga_mobil='"+harga.Harga+"' WHERE jenis_mobil='"+jenis.Jenis+"'";
    let query = conn.query(sql, (err, result) => {
        console.log(JSON.stringify(
            {
                "status" : 200,
                "error" : null,
                "response" : result
            }
        ));
        console.log(sql)
        if(result.affectedRows == "0") {
            res.send ("Gagal Edit Data")
        }
        else {
            res.send ("Berhasil Mengedit Data")
        }
    })
});

//booking
app.post('/booking', function(req, res) {
    console.log("POST request /booking");
    let username = { user: req.body.username };
    let telepon = { telp: req.body.telepon };
    let waktu = { wkt: req.body.waktu };
    let jenis = { jns: req.body.jenis };
  
    // Check if there are enough available vehicles
    let checkAvailabilityQuery = "SELECT jumlah_mobil FROM list_mobil WHERE jenis_mobil = ?";
    let checkAvailabilityParams = [jenis.jns];
    conn.query(checkAvailabilityQuery, checkAvailabilityParams, (err, availabilityResult) => {
      if (err) {
        console.error(err);
        res.status(500).json({ status: 500, error: 'Internal Server Error', response: null });
      } else {
        if (availabilityResult.length > 0 && availabilityResult[0].jumlah_mobil > 0) {
          // Insert the booking data
          let insertBookingQuery = "INSERT INTO booking (username, telepon, waktu, jenis_mobil) VALUES (?, ?, ?, ?)";
          let insertBookingParams = [username.user, telepon.telp, waktu.wkt, jenis.jns];
          conn.query(insertBookingQuery, insertBookingParams, (err, bookingResult) => {
            if (err) {
              console.error(err);
              res.status(500).json({ status: 500, error: 'Internal Server Error', response: null });
            } else {
              if (bookingResult.affectedRows > 0) {
                // Decrease the available vehicle count
                let updateAvailabilityQuery = "UPDATE list_mobil SET jumlah_mobil = jumlah_mobil - 1 WHERE jenis_mobil = ?";
                let updateAvailabilityParams = [jenis.jns];
                conn.query(updateAvailabilityQuery, updateAvailabilityParams, (err, updateResult) => {
                  if (err) {
                    console.error(err);
                    res.status(500).json({ status: 500, error: 'Internal Server Error', response: null });
                  } else {
                    console.log("Booking data inserted.");
                    res.status(200).json(bookingResult.insertId);
			        //res.send ("Berhasil Booking, booking id = "+result.insertId)
                  }
                });
              } else {
                res.status(500).json({ status: 500, error: 'Internal Server Error', response: null });
              }
            }
          });
        } else {
          res.status(400).json({ status: 400, error: 'Bad Request', response: 'Jenis mobil tidak tersedia' });
        }
      }
    });
  });

//cancel booking
// cancel booking
app.post('/cancelbooking', function(req, res) {
    console.log('POST request /cancelbooking');
    let username = req.body.username;
    let bookingid = req.body.bookingid;
    let checkBookingQuery = "SELECT * FROM booking WHERE username = ? AND bookingid = ?";
    let checkBookingParams = [username, bookingid];
  
    conn.query(checkBookingQuery, checkBookingParams, (err, bookingResult) => {
        if (err) {
            console.error(err);
            res.status(500).json({ status: 500, error: 'Internal Server Error', response: null });
        } else {
            if (bookingResult.length > 0) {
                let deleteBookingQuery = "DELETE FROM booking WHERE bookingid = ?";
                let deleteBookingParams = [bookingid];
              
                conn.query(deleteBookingQuery, deleteBookingParams, (err, deleteResult) => {
                    if (err) {
                        console.error(err);
                        res.status(500).json({ status: 500, error: 'Internal Server Error', response: null });
                    } else {
                        if (deleteResult.affectedRows > 0) {
                            let updateAvailabilityQuery = "UPDATE list_mobil SET jumlah_mobil = jumlah_mobil + 1 WHERE jenis_mobil = ?";
                            let updateAvailabilityParams = [bookingResult[0].jenis_mobil];
                          
                            conn.query(updateAvailabilityQuery, updateAvailabilityParams, (err, updateResult) => {
                                if (err) {
                                    console.error(err);
                                    res.status(500).json({ status: 500, error: 'Internal Server Error', response: null });
                                } else {
                                    console.log("Booking canceled. Restocked the vehicle.");
                                    res.status(200).json({ status: 200, error: null, response: 'Booking canceled. Vehicle restocked.' });
                                }
                            });
                        } else {
                            res.status(500).json({ status: 500, error: 'Internal Server Error', response: null });
                        }
                    }
                });
            } else {
                res.status(404).json({ status: 404, error: 'Not Found', response: 'Booking not found.' });
            }
        }
    });
});

var server = app.listen(7000, function(){
    var host = server.address().address;
    var port = server.address().port;
    console.log("Express app listening at http://%s:%s", host, port);
})