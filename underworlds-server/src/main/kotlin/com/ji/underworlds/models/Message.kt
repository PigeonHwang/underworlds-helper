package com.ji.underworlds.models

import com.fasterxml.jackson.annotation.JsonManagedReference

class Message (
    var type: String = "",
    var data: Any = Any()
)