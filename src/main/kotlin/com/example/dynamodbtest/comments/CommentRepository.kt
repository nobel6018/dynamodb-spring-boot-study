package com.example.dynamodbtest.comments

import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.springframework.data.repository.CrudRepository

@EnableScan
interface CommentRepository : CrudRepository<Comment2, String> {
}