# SVGAGlidePlugin
> Glide Integration Library for Playing .svga Animation Files

---

## SVGA
you can know more at [svga.io][1] .

## Feature

- [x] Thread pool management.
- [x] Resource cache management（Memory & Disk).
- [x] Users only need to care about loading and displaying a resource from Url, no matter what format it is, JPG, GIF or SVGA.
- [x] Support [RePlugin][2] even with this [ISSUE][3]. 

## Usage

```kotlin
fun loadSVGAFromNetwork(v: View) {
        Glide.with(this)
            .load("https://github.com/yyued/SVGA-Samples/blob/master/kingset.svga?raw=true")
            .into(iv_img)
}

fun loadSVGAFromAssets(v: View) {
        Glide.with(this)
            .load("file:///android_asset/angel.svga")
            .into(iv_img)
}

fun loadSVGAFromNetworkAndAddText(v: View) {
        GlideApp.with(this)
            .asSVGA()
            .load("https://github.com/yyued/SVGA-Samples/blob/master/kingset.svga?raw=true")
            .into(SVGATarget(iv_img, requestDynamicItemWithSpannableText()))
}
```


  [1]: http://svga.io/
  [2]: https://github.com/Qihoo360/RePlugin
  [3]: https://github.com/Qihoo360/RePlugin/issues/351
