package com.ramanbyte.emla.data_layer.network.exception

import java.io.IOException

class ApiException(message : String , val code : Int = 0) : IOException(message)
class NoDataException(message : String , val code : Int = 0) : IOException(message)
class NoInternetException(message : String , val code : Int = 0) : IOException(message)
class ResourceNotFound(message : String , val code : Int = 0) : IOException(message)