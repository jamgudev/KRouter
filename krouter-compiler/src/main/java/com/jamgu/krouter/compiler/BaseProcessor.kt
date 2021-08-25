package com.jamgu.krouter.compiler

import com.jamgu.krouter.compiler.utils.Constants.KEY_MODULE_NAME
import com.jamgu.krouter.compiler.utils.Constants.NO_MODULE_NAME_TIPS
import com.jamgu.krouter.compiler.utils.Constants.PREFIX_OF_LOGGER
import com.jamgu.krouter.compiler.utils.Logger
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.util.Elements
import javax.lang.model.util.Types


/**
 * Created by jamgu on 2021/08/24
 */
abstract class BaseProcessor: AbstractProcessor() {

    protected var mFiler: Filer? = null
    protected var mLogger: Logger? = null
    protected var mTypeUtils: Types? = null
    protected var mElementUtils: Elements? = null

    // Module name, maybe its 'app' or others
    protected var moduleName: String? = null

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        processingEnv?.let {
            mFiler = it.filer
            mTypeUtils = it.typeUtils
            mElementUtils = it.elementUtils
            mLogger = Logger(it.messager)

            // Attempt to get user configuration [moduleName]
            val options = it.options
            if (options.isNotEmpty()) {
                moduleName = options[KEY_MODULE_NAME]
            }

            if (moduleName?.isNotEmpty() == true) {
                moduleName = moduleName?.replace("[^0-9a-zA-Z_]+".toRegex(), "")
                mLogger?.info("The user has configuration the module name, it was [$moduleName]")
            } else {
                mLogger?.error(NO_MODULE_NAME_TIPS)
                throw RuntimeException("$PREFIX_OF_LOGGER>>> No module name, for more information, look at gradle log.")
            }
        }
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return HashSet<String>().apply { add(KEY_MODULE_NAME) }
    }
}