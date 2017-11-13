//
//  ViewController.swift
//  GoEasy
//
//  Created by Haider Ali on 11/8/17.
//

import UIKit
import Foundation
import CoreLocation
import GoogleMaps
import GooglePlaces
import Alamofire
import SwiftyJSON
struct picks{
    var lat = 0.0
    var lng = 0.0
    var count  = ""
    var name = ""
    init(lat: Double, lng: Double,count : String, name :String) {
        self.lat = lat
        self.lng = lng
        self.count = count
        self.name = name
    }
}
class ViewController: UIViewController {
    var placesClient: GMSPlacesClient!
    var picksData: [picks] = []
    var userLocation:CLLocationCoordinate2D = CLLocationCoordinate2D(latitude: 0, longitude: 0)
    var apiServerKey = "AIzaSyC0bA3tGmyt0FWmDMhlfm8YMZQDpNmRq-A"
    var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
    var inp :InputStream?
    var out :OutputStream?
    override func viewDidLoad() {
        super.viewDidLoad()
        placesClient = GMSPlacesClient.shared()
        userLocation.latitude=28.6315
        userLocation.longitude=77.2167
        let addr = "127.0.0.1"
        let port = 9999
        
        Stream.getStreamsToHost(withName: addr, port: port, inputStream: &inp, outputStream: &out)
        out!.open()
        inp!.open()
        var loc = "0 28.6315 77.2167\n"
        out!.write(loc, maxLength: loc.count)
        
        // Do any additional setup after loading the view, typically from a nib.
    }
    func sendData(query: String){
        let req = "\(url)\(userLocation.latitude),\(userLocation.longitude)&radius=4000&type=\(query)&keyword=cruise&key=AIzaSyC0bA3tGmyt0FWmDMhlfm8YMZQDpNmRq-A"
        
        Alamofire.request(req).responseJSON { (response) in
            switch response.result{
            case let .success(data):
                let json = JSON(data)
                let length = json["results"].count
                for var i in 0..<length{
                    print(json["results"][i]["geometry"]["location"])
                    print(json["results"][i]["name"])
                    
                    var loc = "1 \(json["results"][i]["geometry"]["location"]["lat"]) \(json["results"][i]["geometry"]["location"]["lng"])\n"
                    self.out!.write(loc, maxLength: loc.count)
                    let queue = DispatchQueue.global(qos: DispatchQoS.QoSClass.default)
               
                    var c : String = ""
                    queue.sync() {
                        let bufferSize = 1024
                        var inputBuffer = Array<UInt8>(repeating: 0, count:bufferSize)
                        while true {
                            _ = self.inp!.read(&inputBuffer, maxLength: bufferSize)
                            
                            // Here get string from byte Array
                            let responseString = NSString(bytes: inputBuffer, length: inputBuffer.count, encoding: String.Encoding.utf8.rawValue)! as String
                            print("Data from Stream = \(responseString)")
                            c = responseString
                            break
                        }
                    }
                    let lat =  json["results"][i]["geometry"]["location"]["lat"].double
                    let lng =  json["results"][i]["geometry"]["location"]["lng"].double
                    let name = json["results"][i]["name"].string
                  
                    let a: picks = picks(lat: lat! , lng: lng!, count: c, name: name!)
                  
                    self.picksData.append(a)
                    
                    
                }
                self.performSegue(withIdentifier: "performSegue", sender: nil)
            case .failure(_):
                print("Chutiya Chauhan")
            }
        }
    }
    
    @IBAction func onClickBank(_ sender: Any) {
        sendData(query: "bank")
    }
    @IBAction func onClickHospital(_ sender: Any) {
        sendData(query: "hospital")
    }
    @IBAction func onClickRestaurant(_ sender: Any) {
         sendData(query: "restaurant")
    }
    @IBAction func onClickMalls(_ sender: Any) {
         sendData(query: "shopping_mall")
    }
    @IBAction func onClickATM(_ sender: Any) {
        sendData(query: "atm")
    }
    @IBAction func onClickGym(_ sender: Any) {
        sendData(query: "gym")
    }
    
    @IBAction func autocompleteClicked(_ sender: UIButton) {
        let autocompleteController = GMSAutocompleteViewController()
        autocompleteController.delegate = self
        present(autocompleteController, animated: true, completion: nil)
       
    }
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        let backgroundImage = UIImageView(frame: UIScreen.main.bounds)
        backgroundImage.image = UIImage(named: "bg")
        backgroundImage.contentMode = UIViewContentMode.scaleAspectFill
        self.view.insertSubview(backgroundImage, at: 0)
        /**/
    }
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "performSegue" {
           
            let controller = segue.destination as! MapViewController
            controller.picksData = self.picksData
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}
extension ViewController: GMSAutocompleteViewControllerDelegate {
    
    // Handle the user's selection.
    func viewController(_ viewController: GMSAutocompleteViewController, didAutocompleteWith place: GMSPlace) {
        var loc = "1 \(place.coordinate.latitude) \(place.coordinate.longitude)\n"
        self.out!.write(loc, maxLength: loc.count)
        let queue = DispatchQueue.global(qos: DispatchQoS.QoSClass.default)
        var c : String = ""
        queue.sync() {
            let bufferSize = 1024
            var inputBuffer = Array<UInt8>(repeating: 0, count:bufferSize)
            while true {
                _ = self.inp!.read(&inputBuffer, maxLength: bufferSize)
                
                // Here get string from byte Array
                let responseString = NSString(bytes: inputBuffer, length: inputBuffer.count, encoding: String.Encoding.utf8.rawValue)! as String
                print("Data from Stream = \(responseString)")
                c = responseString
                break
            }
        }
        let lat =  place.coordinate.latitude
        let lng =  place.coordinate.longitude
        let name = place.name
        
        let a: picks = picks(lat: lat , lng: lng, count: c, name: name)
        self.picksData.append(a)
        
        dismiss(animated: true) {
            self.performSegue(withIdentifier: "performSegue", sender: nil)
        }
        
    }
    
    func viewController(_ viewController: GMSAutocompleteViewController, didFailAutocompleteWithError error: Error) {
        // TODO: handle the error.
        print("Error: ", error.localizedDescription)
    }
    
    // User canceled the operation.
    func wasCancelled(_ viewController: GMSAutocompleteViewController) {
        dismiss(animated: true, completion: nil)
    }
    
    // Turn the network activity indicator on and off again.
    func didRequestAutocompletePredictions(_ viewController: GMSAutocompleteViewController) {
        UIApplication.shared.isNetworkActivityIndicatorVisible = true
    }
    
    func didUpdateAutocompletePredictions(_ viewController: GMSAutocompleteViewController) {
        UIApplication.shared.isNetworkActivityIndicatorVisible = false
    }
    
}

