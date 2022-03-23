package com.example.dynamodbtest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DynamodbTestApplication

fun main(args: Array<String>) {
    runApplication<DynamodbTestApplication>(*args)
}
