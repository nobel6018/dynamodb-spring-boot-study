package com.example.dynamodbtest.charging

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.*
import com.amazonaws.services.dynamodbv2.util.TableUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.time.OffsetDateTime
import java.util.*
import kotlin.collections.HashMap

@SpringBootTest
internal class ChargingStationTest {

    lateinit var amazonDynamoDB: AmazonDynamoDB

    @Value("\${cloud.aws.credentials.access-key}")
    private lateinit var accessKey: String

    @Value("\${cloud.aws.credentials.secret-key}")
    private lateinit var secretKey: String


    @BeforeEach
    fun setUp() {
        val basicAWSCredentials =
            BasicAWSCredentials(accessKey, secretKey)
        val awsStaticCredentialsProvider = AWSStaticCredentialsProvider(basicAWSCredentials)
        val endpointConfiguration =
            EndpointConfiguration("https://dynamodb.ap-northeast-2.amazonaws.com", "ap-northeast-2")

        amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withCredentials(awsStaticCredentialsProvider)
            .withEndpointConfiguration(endpointConfiguration)
            .build()
    }

    @Test
    @Disabled
    fun testCreateTable() {
        val createTableRequest = CreateTableRequest()
            .withAttributeDefinitions(
                AttributeDefinition("id", ScalarAttributeType.S),
                AttributeDefinition("mentionId", ScalarAttributeType.N),
                AttributeDefinition("createdAt", ScalarAttributeType.S)
            )
            .withTableName("Comment")
            .withKeySchema(KeySchemaElement("id", KeyType.HASH))
            .withGlobalSecondaryIndexes(
                GlobalSecondaryIndex()
                    .withIndexName("byMentionId")
                    .withKeySchema(
                        KeySchemaElement("mentionId", KeyType.HASH),
                        KeySchemaElement("createdAt", KeyType.RANGE)
                    )
                    .withProjection(Projection().withProjectionType(ProjectionType.ALL))
                    .withProvisionedThroughput(ProvisionedThroughput(1L, 1L))
            )
            .withProvisionedThroughput(ProvisionedThroughput(1L, 1L))

        val isTableCreated = TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest)

        assertThat(isTableCreated).isEqualTo(true)
    }

    @Test
    @Disabled
    fun testPutItemAfterCreatedTable() {
        val item = HashMap<String, AttributeValue>()
        item["id"] = AttributeValue().withS(UUID.randomUUID().toString())
        item["mentionId"] = AttributeValue().withN("1")
        item["content"] = AttributeValue().withS("comment content")
        item["deleted"] = AttributeValue().withBOOL(false)
        item["createdAt"] = AttributeValue().withS(OffsetDateTime.now().toString())

        val putItemRequest = PutItemRequest()
            .withTableName("Comment")
            .withItem(item)

        val putItemResult = amazonDynamoDB.putItem(putItemRequest)

        assertThat(putItemResult.sdkHttpMetadata.httpStatusCode).isEqualTo(200)
    }

    @Test
    @Disabled
    fun testGetItemAfterCreatedTable() {
        // given
        val key = HashMap<String, AttributeValue>()
        key["id"] = AttributeValue().withS("235fe7ad-db1c-4268-bcc7-f61f8185c194")

        val item = HashMap<String, AttributeValue>()
        item["mentionId"] = AttributeValue().withN("1")
        item["content"] = AttributeValue().withS("comment content")

        // when
        val getItemRequest = GetItemRequest()
            .withTableName("Comment")
            .withKey(key)

        val getItemResult = amazonDynamoDB.getItem(getItemRequest)

        // then
        assertThat(getItemResult.item["mentionId"]).isEqualTo(item["mentionId"])
        assertThat(getItemResult.item["content"]).isEqualTo(item["content"])
    }

    @Test
    @Disabled
    fun testDeleteItem() {
        // given
        val key = HashMap<String, AttributeValue>()
        key["id"] = AttributeValue().withS("235fe7ad-db1c-4268-bcc7-f61f8185c194")

        // when
        val deleteItemRequest = DeleteItemRequest()
            .withTableName("Comment")
            .withKey(key)

        val deleteItemResult = amazonDynamoDB.deleteItem(deleteItemRequest)

        // then
        assertThat(deleteItemResult.sdkHttpMetadata.httpStatusCode).isEqualTo(200)
    }

}

