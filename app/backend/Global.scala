package backend

import javax.inject._

import scala.util.{Failure,Success}

import scala.concurrent.{Await,ExecutionContext}
import scala.concurrent.duration._

import play.api._
import play.api.libs.json.Json
import play.api.{Logger, Application}
import com.google.inject.AbstractModule
import play.api.inject.{ApplicationLifecycle, Binding, Module}

import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

/**
 * MongoDB module.
 */
@Singleton
final class GlobalModule(
  environment: Environment, configuration: Configuration)
    extends AbstractModule {

  def configure() = bind(classOf[Global]).asEagerSingleton()
}

class Global @Inject() (
  appCycle: ApplicationLifecycle,
  reactiveMongoApi: ReactiveMongoApi,
  implicit val ec: ExecutionContext) {

  private def collection = reactiveMongoApi.database.map(
    _.collection[JSONCollection]("posts"))

  private def posts = List(
    Json.obj(
      "text" -> "Have you heard about the Web Components revolution?",
      "username" -> "Eric",
      "avatar" -> "../images/avatar-01.svg",
      "favorite" -> false
    ),
    Json.obj(
      "text" -> "Loving this Polymer thing.",
      "username" -> "Rob",
      "avatar" -> "../images/avatar-02.svg",
      "favorite" -> false
    ),
    Json.obj(
      "text" -> "So last year...",
      "username" -> "Dimitri",
      "avatar" -> "../images/avatar-03.svg",
      "favorite" -> false
    ),
    Json.obj(
      "text" -> "Pretty sure I came up with that first.",
      "username" -> "Ada",
      "avatar" -> "../images/avatar-07.svg",
      "favorite" -> false
    ),
    Json.obj(
      "text" -> "Yo, I heard you like components, so I put a component in your component.",
      "username" -> "Grace",
      "avatar" -> "../images/avatar-08.svg",
      "favorite" -> false
    ),
    Json.obj(
      "text" -> "Centralize, centrailize.",
      "username" -> "John",
      "avatar" -> "../images/avatar-04.svg",
      "favorite" -> false
    ),
    Json.obj(
      "text" -> "Has anyone seen my cat?",
      "username" -> "Zelda",
      "avatar" -> "../images/avatar-06.svg",
      "favorite" -> false
    ),
    Json.obj(
      "text" -> "Decentralize!",
      "username" -> "Norbert",
      "avatar" -> "../images/avatar-05.svg",
      "favorite" -> false
    )
  )

  private def onStart() {
    Logger.info("Application has started")

    Await.result(
      collection.flatMap(_.bulkInsert(posts.toStream, ordered = true)).andThen {
        case Failure(reason) =>
          Logger.error("Fails to initialize database", reason)

        case _ => Logger.info("Database was initialized")
      }, 10.seconds)
  }

  appCycle.addStopHook { () =>
    Logger.info("Application shutdown...")

    collection.flatMap(_.drop(false)).
      map(_ => Logger.info("Database collection dropped"))
  }

  onStart()
}
