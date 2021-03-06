package controllers
import play.api.Logger
import play.api.Play.current
import play.api.mvc._
import services._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.concurrent.Promise
import scala.concurrent.Future
import java.text.SimpleDateFormat
import views.html.defaultpages.badRequest
import java.util.Date
import play.api.libs.json.Json._
import models.Extraction
import api.WithPermission
import api.Permission
import javax.inject.Inject
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray
import services.ExtractorService
import services.ExtractionRequestsService
import models.ExtractionInfoSetUp
import play.api.libs.json._
import java.util.Calendar
import java.net.InetAddress


class ExtractionInfo @Inject() (extractors: ExtractorService, dtsrequests: ExtractionRequestsService) extends SecuredController {

  /**
   * Directs currently running extractor's server IPs to the webpage
   */

  def getExtractorServersIP() = SecuredAction(authorization = WithPermission(Permission.Public)) { implicit request =>
    Async {
      for {
        x <- ExtractionInfoSetUp.updateExtractorsInfo()
        status <- x
      } yield {

        Logger.debug("Update Status:" + status)
        val list_servers = extractors.getExtractorServerIPList()
        var jarr = new JsArray()
        var list_servers1=List[String]()
        list_servers.map {
          ls =>
            Logger.debug("Server Name:  " + ls.substring(1, ls.size-1))
            jarr = jarr :+ (Json.parse(ls))
            list_servers1=ls.substring(1, ls.size-1)::list_servers1
            }
        Logger.debug("Json array for list of extractors server ips----" + jarr.toString)
        Ok(views.html.extractorsServersIP(list_servers1,list_servers1.size))
      }
    }
  }
  
/**
 * Directs currently running extractors information to the webpage 
 */
  def getExtractorNames() = SecuredAction(authorization = WithPermission(Permission.Public)) { implicit request =>

    val list_names = extractors.getExtractorNames()
    var jarr = new JsArray()
    var list_names1=List[String]()
    list_names.map {
      ls =>
        Logger.debug("Extractor Name:  " + ls)
        jarr = jarr :+ (Json.parse(ls))
        list_names1=ls.substring(1, ls.size-1)::list_names1

    }
    Logger.debug("Json array for list of extractor names----" + jarr.toString)
    Ok(views.html.extractors(list_names1,list_names1.size))

  }
  
/**
 * Directs input type supported by currently running extractors information to the webpage
 */
  def getExtractorInputTypes() = SecuredAction(authorization = WithPermission(Permission.Public)) { implicit request =>

    val list_inputtypes = extractors.getExtractorInputTypes()
    var jarr = new JsArray()
    var list_inputtypes1=List[String]()
    list_inputtypes.map {
      ls =>
        Logger.debug("Extractor Input Type:  " + ls)
        jarr = jarr :+ (Json.parse(ls))
        list_inputtypes1=ls.substring(1, ls.size-1)::list_inputtypes1

    }
    Logger.debug("Json array for list of input types supported by extractors----" + jarr.toString)
    Ok(views.html.extractorsInputTypes(list_inputtypes1,list_inputtypes1.size))

  }
  
  /**
   * Directs DTS extractions requests information to the webpage
   */
   def getDTSRequests() = SecuredAction(authorization = WithPermission(Permission.Public)) { implicit request =>

    var list_requests = dtsrequests.getDTSRequests()
    var startTime = models.ServerStartTime.startTime
    var currentTime = Calendar.getInstance().getTime()
    Ok(views.html.extractionRequests(list_requests, list_requests.size, startTime, currentTime))
  }
   
   /**
   * DTS Bookmarklet page
   */
   def getBookmarkletPage() = SecuredAction(authorization = WithPermission(Permission.Public)) { implicit request =>

      Ok(views.html.dtsbookmarklet(Utils.baseUrl(request)))
  }

  /**
   * DTS Chrome Extension page
   */
  def getExtensionPage() = SecuredAction(authorization = WithPermission(Permission.Public)) { implicit request =>
    val configuration = play.api.Play.configuration
    val url = Utils.baseUrl(request)
    var hostname = if (url.indexOf('.') == -1) { url.substring(url.indexOf('/') + 2, url.lastIndexOf(':')) } else { url.substring(url.indexOf('/') + 2, url.indexOf('.')) }
    Logger.debug(" url= " + url + "  hostname " + hostname)
    val extensionHostUrl = configuration.getString("dts.extension.host").getOrElse("")
    Ok(views.html.dtsExtension(Utils.baseUrl(request), hostname, extensionHostUrl))
  }
}