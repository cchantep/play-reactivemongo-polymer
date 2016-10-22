package controllers

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{ Action, BodyParsers, Call, Controller, Result }

import reactivemongo.bson.{ BSONObjectID, BSONDocument }
import reactivemongo.core.actors.Exceptions.PrimaryUnavailableException
import reactivemongo.api.commands.WriteResult

import play.modules.reactivemongo.{
  MongoController, ReactiveMongoApi, ReactiveMongoComponents
}

class Posts @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends Controller with MongoController with ReactiveMongoComponents {

  import controllers.PostFields._

  def postRepo = new backend.PostMongoRepo(reactiveMongoApi)

  val list = Action.async { implicit request =>
    postRepo.find().map(posts => Ok(Json.toJson(posts.reverse)))
  }

  def like(id: BSONObjectID) =
    Action.async(BodyParsers.parse.json) { implicit request =>
      val value = (request.body \ Favorite).as[Boolean]
      postRepo.update(BSONDocument(Id -> id),
        BSONDocument("$set" -> BSONDocument(Favorite -> value))).
        map(le => Ok(Json.obj("success" -> le.ok)))
    }

  def update(id: BSONObjectID) =
    Action.async(BodyParsers.parse.json) { implicit request =>
      val value = (request.body \ Text).as[String]
      postRepo.update(BSONDocument(Id -> id),
        BSONDocument("$set" -> BSONDocument(Text -> value))).
        map(le => Ok(Json.obj("success" -> le.ok)))
    }

  def delete(id: BSONObjectID) = Action.async {
    postRepo.remove(BSONDocument(Id -> id)).
      map(_ => Redirect(routes.Posts.list()))
  }

  val add = Action.async(BodyParsers.parse.json) { implicit request =>
    val username = (request.body \ Username).as[String]
    val text = (request.body \ Text).as[String]
    val avatar = (request.body \ Avatar).as[String]

    postRepo.save(BSONDocument(
      Text -> text,
      Username -> username,
      Avatar -> avatar,
      Favorite -> false
    )).map(le => Redirect(routes.Posts.list()))
  }
}

object PostFields {
  val Id = "_id"
  val Text = "text"
  val Username = "username"
  val Avatar = "avatar"
  val Favorite = "favorite"
}
