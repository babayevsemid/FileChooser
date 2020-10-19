# Android File Chooser 

### Installation

Add this to your ```build.gradle``` file

```
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
     implementation 'com.github.babayevsemid:FileChooser:0.0.1'
}
```
  
```
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FileChooser.setup(this);
    }
}
```

### Use with Context

 FileListener listener = new FileListener() {
            @Override
            public void newFile(ArrayList<FileModel> files, FileModel fileModel) {
                
            }

            @Override
            public void onChanged(ArrayList<FileModel> files) {
                 
            }

            @Override
            public void deletedFile(boolean isVideo, FileModel fileModel, int position) {
            
            }

            @Override
            public void deletedAllFiles() {
            
            }
        };

        FileChooser chooser = FileChooser.getInstance();
        chooser.addListener(listener);
        chooser.intent(ChooseTypeEnum.CHOOSE_PHOTO);

``` 

### Take video max second //100

``` 
                 FileChooser.getInstance()
                        .intent(ChooseTypeEnum.CHOOSE_VIDEO, 100);
        
```
 
