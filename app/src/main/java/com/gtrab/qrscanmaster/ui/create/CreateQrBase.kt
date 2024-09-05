package com.gtrab.qrscanmaster.ui.create

import androidx.fragment.app.Fragment
import com.gtrab.qrscanmaster.extension.unsafeLazy
import com.gtrab.qrscanmaster.model.schema.Other
import com.gtrab.qrscanmaster.model.schema.Schema

abstract class CreateQrBase : Fragment() {
    protected val parentFragmen by unsafeLazy { requireParentFragment() as FragmentCreateQrMain }
    open fun getBarcodeSchema(): Schema = Other("")

}