package com.example.dynamodbtest.charging

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.model.Projection
import com.amazonaws.services.dynamodbv2.model.ProjectionType
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.util.TableUtils
import com.example.dynamodbtest.comments.Comment2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.time.OffsetDateTime

@SpringBootTest
internal class DynamoDBMapperTest {
    lateinit var amazonDynamoDB: AmazonDynamoDB
    lateinit var dynamoDBMapper: DynamoDBMapper

    @Value("\${cloud.aws.credentials.access-key}")
    private lateinit var accessKey: String

    @Value("\${cloud.aws.credentials.secret-key}")
    private lateinit var secretKey: String

    @BeforeEach
    fun setup() {
        val basicAWSCredentials = BasicAWSCredentials(accessKey, secretKey)
        val awsStaticCredentialsProvider = AWSStaticCredentialsProvider(basicAWSCredentials)
        val endpointConfiguration =
            EndpointConfiguration("https://dynamodb.ap-northeast-2.amazonaws.com", "ap-northeast-2")

        amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withCredentials(awsStaticCredentialsProvider)
            .withEndpointConfiguration(endpointConfiguration)
            .build()

        dynamoDBMapper = DynamoDBMapper(amazonDynamoDB, DynamoDBMapperConfig.DEFAULT)
    }

    @Test
    @Disabled
    fun testCreateTable() {
        // given
        val createTableRequest = dynamoDBMapper
            .generateCreateTableRequest(Comment2::class.java)
            .withProvisionedThroughput(ProvisionedThroughput(1L, 1L))


        // when
        createTableRequest.globalSecondaryIndexes.forEach {
            it.withProvisionedThroughput(ProvisionedThroughput(1L, 1L))
                .withProjection(Projection().withProjectionType(ProjectionType.ALL))
        }

        // then
        assertThat(TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest)).isTrue
    }

    @Test
    fun testSaveItemAfterCreatedTable() {
        // given
        val comment2 = Comment2()
        comment2.mentionId = 1
        comment2.name = "name"
        comment2.content = "example content"
        comment2.createdAt = OffsetDateTime.now()
        comment2.deleted = false

        // when
        dynamoDBMapper.save(comment2)

        // then
    }

    @Test
    @Disabled
    fun testGetItem() {
        // given
        val loadedItem = dynamoDBMapper.load(Comment2::class.java, "acbf0644-8d34-40f3-8dcb-37ddeba2b59a")

        assertThat(loadedItem.content).isEqualTo("example content")
        assertThat(loadedItem.createdAt).isNotNull
        assertThat(loadedItem.deleted).isFalse

        // when

        // then
    }
}