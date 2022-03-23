package com.example.dynamodbtest.charging

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.example.dynamodbtest.comments.Comment2
import com.example.dynamodbtest.comments.CommentRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.OffsetDateTime

@SpringBootTest
internal class DynamoDBRepositoryTest {

    @Autowired
    lateinit var commentRepository: CommentRepository

    @Autowired
    lateinit var amazonDynamoDB: AmazonDynamoDB

    @Autowired
    lateinit var dynamoDBMapper: DynamoDBMapper

    @Test
    fun testSaveItem() {
        // given
        val comment2 = Comment2()
        comment2.id = "test-id"
        comment2.name = "googit"
        comment2.content = "hello world"
        comment2.createdAt = OffsetDateTime.now()

        // when
        val savedComment = commentRepository.save(comment2)

        // then
        println(savedComment)
        assertThat(savedComment.id).isEqualTo("test-id")
        assertThat(savedComment.name).isEqualTo("googit")
    }
}