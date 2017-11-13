//
//  MapViewController.swift
//  GoEasy
//
//  Created by Haider Ali on 11/12/17.
//

import UIKit
import GoogleMaps
import GooglePlaces
import SwiftyJSON
import Alamofire

class MapViewController: UIViewController,GMSMapViewDelegate {
   
    var picksData: [picks] = []
    @IBOutlet var mapView: GMSMapView!
    var mapView1 : GMSMapView!
    override func viewDidLoad() {
        super.viewDidLoad()
      
        let camera = GMSCameraPosition.camera(withLatitude: 28.6315, longitude: 77.2167, zoom: 13.0)
        mapView.camera = camera
        var pos = 0
        for i in 0..<picksData.count{
           
            if(picksData[i].count<picksData[pos].count){
                pos=i
            }
        }
        for i in 0..<picksData.count{
            if i == pos{
                continue
            }
            let marker = GMSMarker()
            marker.position = CLLocationCoordinate2D(latitude: picksData[i].lat, longitude: picksData[i].lng)
            if picksData[i].count.count == 1{
                marker.title = "11"+picksData[i].count
            }
            else{
                marker.title = "1"+picksData[i].count
            }
            marker.snippet = picksData[i].name
            marker.map = mapView
        }
        
        if picksData.count != 0{
            let marker = GMSMarker()
            marker.position = CLLocationCoordinate2D(latitude: picksData[pos].lat, longitude: picksData[pos].lng)
            marker.title = picksData[pos].count
            marker.snippet = picksData[pos].name
            let im = UIImage(named: "3")?.resize(maxWidthHeight: 50)
            
            marker.icon = im
            marker.map = mapView
        }
        if picksData.count != 0{
        let marker1 = GMSMarker()
            marker1.position = CLLocationCoordinate2D(latitude: 28.6315, longitude: 77.2167)
            marker1.title = "Your Position"
            marker1.icon = GMSMarker.markerImage(with: .black)
            marker1.map = mapView
            let path = GMSMutablePath()
            path.addLatitude(28.6315, longitude: 77.2167)
            path.addLatitude(picksData[pos].lat, longitude: picksData[pos].lng)
            let polyline = GMSPolyline(path: path)
            polyline.strokeWidth = 2.0
            polyline.strokeColor = UIColor.blue
            polyline.geodesic = true
            polyline.map = mapView
        }
        // Creates a marker in the center of the map.
       
        // Do any additional setup after loading the view.
       
    }
    private func drowRoute(){
        
       
        
    }
    func backAction() -> Void {
        self.navigationController?.popViewController(animated: true)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
extension UIImage {
    
    func resize(maxWidthHeight : Double)-> UIImage? {
        
        let actualHeight = Double(size.height)
        let actualWidth = Double(size.width)
        var maxWidth = 0.0
        var maxHeight = 0.0
        
        if actualWidth > actualHeight {
            maxWidth = maxWidthHeight
            let per = (100.0 * maxWidthHeight / actualWidth)
            maxHeight = (actualHeight * per) / 100.0
        }else{
            maxHeight = maxWidthHeight
            let per = (100.0 * maxWidthHeight / actualHeight)
            maxWidth = (actualWidth * per) / 100.0
        }
        
        let hasAlpha = true
        let scale: CGFloat = 0.0
        
        UIGraphicsBeginImageContextWithOptions(CGSize(width: maxWidth, height: maxHeight), !hasAlpha, scale)
        self.draw(in: CGRect(origin: .zero, size: CGSize(width: maxWidth, height: maxHeight)))
        
        let scaledImage = UIGraphicsGetImageFromCurrentImageContext()
        return scaledImage
    }
    
}
