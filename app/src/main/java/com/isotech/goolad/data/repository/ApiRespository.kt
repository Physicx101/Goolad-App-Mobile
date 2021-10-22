package com.isotech.goolad.data.repository

import com.isotech.goolad.data.model.Parameter
import com.isotech.goolad.data.network.ApiService
import com.isotech.goolad.data.network.api

class ApiRespository {

    private var services: ApiService = api

     fun predict(parameter: Parameter) = services.predict(parameter)
}