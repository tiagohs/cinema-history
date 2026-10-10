package com.tiagohs.helpers.tools

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

/** Módulo do Glide do app (as imagens antes vinham do Firebase Storage; agora vêm por URL, ver MediaUrls). */
@GlideModule
class FirebaseAppStorageModule : AppGlideModule()
