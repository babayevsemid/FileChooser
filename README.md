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
    implementation 'com.github.babayevsemid:FileChooser:1.5.0'
     
     //required 
    implementation "androidx.activity:activity-ktx:1.4.0"
    implementation "androidx.fragment:fragment-ktx:1.4.0"
}
```
### Use in activity

```
val fileChooser = FileChooserActivity(this) 
 fileChooser.fileLiveData
            .observe(this, Observer {
               //Selected your photo
               //it.path
               //it.type
            })
 fileChooser.requestFile(FileTypeEnum.CHOOSE_PHOTO)

``` 

### Use in fragment with check permission

```
val fileChooser = FileChooserFragment(this)
fileChooser.permissionLiveData
            .observe(viewLifecycleOwner){
                if (it) {
                    //Granted
                } else {
                    //Deny
                }
            })
 fileChooser.fileLiveData
            .observe(viewLifecycleOwner){
               //Created your photo
               //it.path
               //it.type
            })
 fileChooser.requestFile(FileTypeEnum.TAKE_PHOTO)

``` 
### Use in java

```
 FileChooserActivity fileChooser = new FileChooserActivity(this);
 fileChooser.fileLiveData
        .observe(viewLifecycleOwner){fileModel->
            Log.e("filePath", fileModel.getPath());

            File file = new File(fileModel.getPath());

            //Use file
        });

 //CHOOSE_PHOTO
 fileChooser.requestFile(FileTypeEnum.CHOOSE_PHOTO, 0);
``` 

### Delete the created files when the application is created
```
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        FileChooserFragment.deleteTakeFiles(this)
    }
}
```

### FileTypeEnum

``` 
  enum class FileTypeEnum {
        CHOOSE_VIDEO,
        CHOOSE_PHOTO, 
        TAKE_PHOTO
  }
        
```
 

### You can check also other permissions

``` 
    fileChooser.manualPermissionLiveData
            .observe(this, Observer {
                println("Permission isGranted $it")
            })
    fileChooser.requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        
```


``` 
    fileChooser.permissionMultiSharedFlow
            .asLiveData()
            .observe(viewLifecycleOwner){
                println("Permission isGranted $it")
            })
            
    fileChooser.multiRequestPermission(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
        
``` 
 
