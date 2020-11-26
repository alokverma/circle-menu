# circle-menu
This is a view group in which you can pass list of menu items and those items will be drawn under circular path.

[![license](https://img.shields.io/github/license/DAVFoundation/captain-n3m0.svg?style=flat-square)](https://github.com/alokverma/circle-menu/blob/main/LICENSE)

# Sample Apk

https://github.com/alokverma/circle-menu/blob/main/app/apk/app-debug.apk

# Demo 
  <p align="center">
  <img src="https://user-images.githubusercontent.com/7018540/100072143-e1cdd300-2e61-11eb-8389-7f989ba866a6.gif" width="200" title="vertical Menu"/>
  <img src="https://user-images.githubusercontent.com/7018540/99903389-13239300-2cea-11eb-9890-7e128c2d7947.gif" width="200" title="Circle Menu"/>
  <img src="https://user-images.githubusercontent.com/7018540/100340890-d9140300-3001-11eb-8115-c1d929cfcbbd.gif" width="200" title="arc_menu"/>
  <img src="https://user-images.githubusercontent.com/7018540/100340908-e03b1100-3001-11eb-8293-0c7e1e486fcd.gif" width="200" title="half_circle"/>
  </p>


# How to Use
To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

gradle

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.alokverma:circle-menu:1.1'
	}

# Code 

       <com.akki.circlemenu.CircleMenu
          android:id="@+id/circle_menu"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          app:menu_background_color="@array/menu_back_colors"
          app:menu_close_duration="300"
          app:menu_icons="@array/menus"
          app:menu_open_duration="400"
	  app:menu_orientation="circle"
          app:menu_radius="100dp">
        
# Attributes that you can use to customize this view group
      app:menu_icons = arrays of vector/png menu items
      app:menu_background_color = array of background tint for those menus
      app:menu_open_duration = open animation duration of circle
      app:menu_close_duration = close animation duration of circle
      app:menu_radius= radius of circle
      app:menu_orientation="circle|vertical|half_circle|arc_left|arc_right"
      
   In your activity/fragment, you can pass setOnMenuItemClickListener and you can get all menu item click event in onMenuItemClicked function.
   
      CircleMenu.setOnMenuItemClickListener(this)
      override fun onMenuItemClicked(id: Int) {
        when (id) {
            R.drawable.ic_baseline_delete_forever_24 -> showToast("Delete Button clicked")
            R.drawable.ic_baseline_person_search_24 -> showToast("Person Button clicked")
            R.drawable.ic_baseline_settings_24 -> showToast("Setting Button clicked")
            R.drawable.ic_baseline_edit_location_24 -> showToast("Location Button clicked")
        }
    }
    


# You can connect me at
[![Linkedin](https://i.stack.imgur.com/gVE0j.png) LinkedIn](https://www.linkedin.com/in/alok-verma-73882666/)
&nbsp;

If you like my work, please give it a star and let me know if you facing any query while implementing this.
