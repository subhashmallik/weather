package com.model.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.model.weather.model.APIResponse
import com.model.weather.network.ApiCallable
import com.model.weather.network.ApiClient
import com.model.weather.ui.main.MainViewModel
import com.model.weatherapplication.models.WeatherData
import junit.framework.Assert
import junit.framework.Assert.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.net.HttpURLConnection


@RunWith(MockitoJUnitRunner::class)
class RequestViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel
    private lateinit var apiCall: ApiCallable

    @Mock
    private  lateinit var apiObserver: Observer<APIResponse<WeatherData>>

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = MainViewModel()
        viewModel.getTemp(12.9716,77.5946,"2fd75483d3a3de115e482fb98284e341","metric").observeForever(apiObserver)
        mockWebServer = MockWebServer()
        mockWebServer.start()
        apiCall = ApiClient.getClient()?.create(ApiCallable::class.java)!!
    }

    @Test
    fun `read sample success json file`(){
        val reader = MockResponseFileReader("success_response.json")
        Assert.assertNotNull(reader.content)
    }

    @Test
    fun `fetch details and check response Code 200 returned`(){
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("success_response.json").content)
        mockWebServer.enqueue(response)
        // Act
        val  actualResponse = apiCall.getTemp(12.9716,77.5946,"2fd75483d3a3de115e482fb98284e341","metric").execute()
        // Assert
        assertEquals(response.toString().contains("200"),actualResponse.code().toString().contains("200"))
    }


    @After
    fun tearDown() {
        viewModel.getTemp(12.9716,77.5946,"2fd75483d3a3de115e482fb98284e341","metric").removeObserver(apiObserver)
        mockWebServer.shutdown()
    }



}