package com.jaitlapps.differ.lexical.impl

import com.google.common.collect.ImmutableSet
import com.jaitlapps.differ.lexical.LexicalAnalyzer
import com.jaitlapps.differ.model.*
import com.jaitlapps.differ.model.token.*
import com.jaitlapps.differ.reader.Reader

class DifferLexicalAnalyzer(private val reader: Reader) : LexicalAnalyzer {

    private val EXPRESSIONS = ImmutableSet.builder<String>().add("+").add("-").add("*").add("/").add("^").add("]").add("[").build()
    private var prevToken: Token? = null

    override fun nextToken(): Token {
        val token: Token;

        val word = reader.readNextWord()

        if (word != null) {
            token = determineToken(word)
        } else {
            token = Token(Word(0, 0, "", ""), TokenType.Eof)
        }

        prevToken = token

        return token
    }

    private fun determineToken(word: Word): Token {
        val tokenKeyword = tryDetermineKeyword(word)
        if(tokenKeyword != null) {
            return KeywordToken(word, TokenType.Keyword, tokenKeyword)
        }

        val tokenMethod = tryDetermineMethod(word)
        if (tokenMethod != null) {
            return MethodToken(word, TokenType.Method, tokenMethod)
        }

        val symbolMethod = tryDetermineSymbol(word)
        if (symbolMethod != null) {
            return SymbolToken(word, TokenType.Symbol, symbolMethod)
        }

        val coefficient = tryDetermineCoefficient(word)
        if (coefficient) {
            return Token(word, TokenType.Coefficient)
        }

        val number = tryDetermineNumber(word)
        if (number != null) {
            return number
        }

        return Token(word, TokenType.Unknown)
    }

    private fun tryDetermineKeyword(word: Word): KeywordType? {
        return KeywordType.KEYWORDS[word.capitalizedWord]
    }

    private fun tryDetermineMethod(word: Word): MethodType? {
        return MethodType.METHODS[word.capitalizedWord]
    }

    private fun tryDetermineSymbol(word: Word): SymbolType? {
        return SymbolType.SYMBOLS[word.capitalizedWord]
    }

    private fun tryDetermineCoefficient(word: Word): Boolean {
        return word.capitalizedWord.matches(Regex("[a-z]"))
    }

    private fun tryDetermineNumber(word: Word): Token? {
        if (word.capitalizedWord.matches(Regex("\\d+"))) {
            return NumberToken(word, TokenType.Integer, word.capitalizedWord.toInt())
        }

        if (word.capitalizedWord.matches(Regex("\\d+\\.\\d+"))) {
            return NumberToken(word, TokenType.Double, word.capitalizedWord.toDouble())
        }

        return null
    }
}