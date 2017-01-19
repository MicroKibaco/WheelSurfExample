抽奖转盘

- surface view extends view
- 其实View是在UI线程中绘制,surfaceview是在子线程中对自己进行绘制,优势:避免造成UI线程阻塞,其实在我们surfaceView中包含一个可以专门用于绘制的surfaceView,surfaceView包含一个canvaceVIew,如何获取Cnavas?
  - [ ] getHolder—>surfaceHolder
        Holder可以拿到一个canvacs--->管理surfaceView的生命周期
        
        surfaceCreated
        
        SurfaceChanged
        
        SurfaceDestroyed

