package com.example.dynamodbtest.charging

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.*
import com.amazonaws.services.dynamodbv2.util.TableUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Value

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
            EndpointConfiguration("https://dynamodb.ap-northeast-2.amazonaws.com", Regions.AP_NORTHEAST_2.name)

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
}

