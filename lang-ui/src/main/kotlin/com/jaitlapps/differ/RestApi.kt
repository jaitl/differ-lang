package com.jaitlapps.differ

import com.jaitlapps.differ.factory.DifferFactory
import com.jaitlapps.differ.interpreter.exception.InterpreterException
import com.jaitlapps.differ.models.DiffProgram
import com.jaitlapps.differ.models.DifferCompileResult
import com.jaitlapps.differ.services.DataService
import com.jaitlapps.differ.services.TextService
import com.jaitlapps.differ.syntax.exception.SyntaxException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class RestApi {

    @RequestMapping("/bnf")
    fun getBnf(): String {
        return DataService.getBnf();
    }

    @RequestMapping("/program", produces = arrayOf("text/html;charset=UTF-8"))
    fun getProgram(): String {
        return TextService.textToHtml(DataService.getProgram())
    }

    @RequestMapping(value = "/compile", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun compile(@RequestBody program: DiffProgram): DifferCompileResult {
        val proCode = TextService.htmlToText(program.code)
        val result = DifferCompileResult()
        println(proCode)

        val interpreter = DifferFactory.createDifferInterpreter(proCode)
        try {
            val res = interpreter.run();
            result.result = res
        } catch(e: SyntaxException) {
            result.isError = true
            result.textError = e.message
        } catch(e: InterpreterException) {
            result.isError = true
            result.textError = e.message
        } catch(e: Exception) {
            result.isError = true
            result.textError = "Ошибка во время выполнения программы"
        }

        return result
    }
}