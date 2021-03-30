import play.api.libs.json._
import scala.collection.mutable

/**
  * Created by saipramod on 30/10/17.
  */


object jsonEvaluator extends App {

  // custom defined precedence based on logic
  val precedence = Map(
    "(" -> 0,
    "OR" -> 1,
    "AND" -> 2,
    "NOT" -> 3,
    "EXISTS" -> 4,
    "==" -> 4
  )

  val operators: List[String] = List("AND", "OR", "NOT", "==", "EXISTS")
  val operators_list: List[String] = List("AND", "(", ")", "OR", "NOT", "==", "EXISTS")

  val input_json_string: String = scala.io.StdIn.readLine("please enter input json ")
  //val json_string: String = """{"color":"red","size":10,"cost":100.0,"mattress":{"name":"king"},"big":true,"legs":[{"length":4}]}"""
  val json: JsValue = Json.parse(input_json_string)

  val input_json_expression = scala.io.StdIn.readLine("please enter input expression ")
  //var input_expression: String = "$mattress.name == 'king' AND $cost == 100.0"
  val input_expression = input_json_expression.replace("'", "")





  /**
    *converts a given infix human readable expression to machine friendly postfix expression without braces.
    *takes input of List of String and returns rearranged List
    */

  def infix_to_postfix(infix_list: List[String]): List[String] = {
    val stack: mutable.Stack[String] = mutable.Stack[String]()
    val postfix_list: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
    infix_list.foreach {
      item =>
        if (!operators_list.contains(item)) postfix_list += item
        else if (item == "(") stack.push(item)
        else if (item == ")") {
          var top_element = stack.pop()
          while (!top_element.equals("(")) {
            postfix_list += top_element
            top_element = stack.pop()
          }
        }
        else {
          while (stack.nonEmpty && precedence(stack.top) >= precedence(item))
            postfix_list += stack.pop()
          stack.push(item)
        }
    }
    while (stack.nonEmpty)
      postfix_list += stack.pop()

    postfix_list.toList
  }


  /**
    *evaluates postfix expression using stack
    *takes input of List of String and returns true / false
    */

  def evaluate_postfix(postfix_list: List[String]): String = {
    val stack: mutable.Stack[String] = mutable.Stack[String]()

    postfix_list.foreach {
      item =>
        if (operators.contains(item)) {
          val a = stack.pop()
          item match {
            case "AND" =>
              val b = stack.pop()
              stack.push(and_op(a, b))
            case "OR" =>
              val b = stack.pop()
              stack.push(or_op(a, b))
            case "==" =>
              val b = stack.pop()
              stack.push(equals_op(a, b))
            case "NOT" =>
              stack.push(not_op(a))
            case "EXISTS" =>
              stack.push(json.as[JsObject].keys.contains(a.substring(1)).toString)
          }
        }
        else stack.push(item)
    }
    stack.top
  }


  /**
    *@param keys the '.' separated key
    *@param json the Json in JsValue type to look inside
    *returns the value if found else returns junk value "***UNDEFINED_KEY***"
    *
    */

  def get_value_from_json(keys: String, json: JsValue): String = {
    try {
      var path: JsValue = json                 // initial path is defined
      val splitKeys = keys.split("\\.")
      System.out.println(s"split keys is ${splitKeys.mkString(" ")}")
      splitKeys.foreach { key =>
         path = path.\(key).as[JsValue]       // value of path changes as no of nested keys increase
        System.out.println(s"for key $key path is $path")
      }
      path.asOpt[String] match {
        case Some(value) => value
        case _ => path.toString
      }
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        System.out.println("exception in json keys")
        "***UNDEFINED_KEY***"
    }
  }


  def equals_op(a: String, b: String): String = {
    try {
      val json_value = get_value_from_json(b.substring(1), json)
      if (a.equals(json_value)) "true"
      else "false"
    }
    catch {
      case ex: Exception =>
        "false"
    }
  }

  def and_op(a: String, b: String): String = {
    if (a.equals("true") && b.equals("true")) "true"
    else "false"
  }

  def or_op(a: String, b: String): String = {
    if (a.equals("false") && b.equals("false")) "false"
    else "true"
  }

  def not_op(a: String): String = {
    if (a.equals("true")) "false"
    else "true"
  }

  def parseDouble(s: String): Option[Double] = try { Some(s.toDouble) } catch { case _ : Throwable => None }
  def hasDecimalValue(d:Double): Boolean = if(d%1==0) false else true



  System.out.println(s"input expression is $input_expression")
  System.out.println("     ")
  System.out.println(s"json value is $json")

  val words: List[String] = input_expression.split("\\ ").toList
  val words_modified: List[String] = words.map {
    token =>
      if (parseDouble(token).isDefined) {
        if (!hasDecimalValue(parseDouble(token).get))
          parseDouble(token).get.toInt.toString
        else
          parseDouble(token).get.toString
      }
      else token
  }
  System.out.println(s"infix expression ${words_modified.mkString(" ")}")
  val post_fix: List[String] = infix_to_postfix(words_modified)
  System.out.println(s"postfix expression ${post_fix.mkString(" ")}")
  val ans = evaluate_postfix(post_fix)
  System.out.println(s"answer is $ans")


}
