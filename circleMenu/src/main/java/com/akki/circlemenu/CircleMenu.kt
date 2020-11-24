package com.akki.circlemenu

import android.animation.*
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.cos
import kotlin.math.sin


class CircleMenu : FrameLayout, View.OnClickListener {

    private var menuItems: ArrayList<Int>
    private val menuBGColor: ArrayList<Int>
    private lateinit var menuCenterButton: FloatingActionButton
    private val mButtons: ArrayList<View> =
        ArrayList()
    private var isAnimating = false
    private var viewState = MenuState.Close
    private var mRadius = 0f
    private var MENU_DEFAULT_SIZE = 100f
    private var mOpenAnimationDuration: Int
    private var mCloseAnimationDuration: Int
    private var onCircleMenuItemClicked: OnCircleMenuItemClicked? = null
    private var mOrientation: Int? = null


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val ta =
            context.theme.obtainStyledAttributes(attrs, R.styleable.CircleMenu, 0, 0)
        try {
            val iconArrayId = ta.getResourceId(R.styleable.CircleMenu_menu_icons, 0)
            val iconBgColorId = ta.getResourceId(R.styleable.CircleMenu_menu_background_color, 0)
            if (ta.hasValue(R.styleable.CircleMenu_menu_orientation)) {
                mOrientation = ta.getInt(R.styleable.CircleMenu_menu_orientation, 0)
            }
            val colorId = resources.obtainTypedArray(iconBgColorId)
            val density = context.resources.displayMetrics.density
            val defaultRadius: Float = MENU_DEFAULT_SIZE * density
            mRadius =
                ta.getDimension(R.styleable.CircleMenu_menu_radius, defaultRadius)
            mOpenAnimationDuration = ta.getInteger(R.styleable.CircleMenu_menu_open_duration, 500)
            mCloseAnimationDuration =
                ta.getInteger(R.styleable.CircleMenu_menu_close_duration, 500)

            val iconsIds = resources.obtainTypedArray(iconArrayId)
            try {
                menuItems = ArrayList(iconsIds.length())
                menuBGColor = ArrayList(iconsIds.length())
                for (i in 0 until iconsIds.length()) {
                    menuItems.add(iconsIds.getResourceId(i, -1))
                    menuBGColor.add(colorId.getColor(i, Color.WHITE))
                }
            } finally {
                iconsIds.recycle()
                colorId.recycle()

            }
        } finally {
            ta.recycle()
        }
        initLayout()
        initMenuButton(context)
    }

    private fun initMenuButton(context: Context) {
        for (i in 0 until menuItems.size) {
            val button = FloatingActionButton(context)
            button.setImageResource(menuItems[i])
            button.id = menuItems[i]
            button.backgroundTintList = ColorStateList.valueOf(menuBGColor[i])
            button.layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            button.scaleX = 0f
            button.scaleY = 0f
            button.setOnClickListener(this)
            addView(button)
            mButtons.add(button)
        }
    }

    private fun initLayout() {
        LayoutInflater.from(context).inflate(R.layout.menu, this, true)
        menuCenterButton = findViewById(R.id.circle_menu_main_button)
        menuCenterButton.setOnClickListener {
            if (isAnimating) {
                return@setOnClickListener
            }
            if (MenuState.Close.ordinal == viewState.ordinal) {
                openAnimation()
            } else
                closeAnimation()
        }

        if (mOrientation == 1) {
            val lp = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
            menuCenterButton.layoutParams = lp
        } else {
            val lp =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER)
            lp.gravity = Gravity.CENTER
            menuCenterButton.layoutParams = lp
        }
        setWillNotDraw(true)
        clipChildren = false
        clipToPadding = false
        val density = context.resources.displayMetrics.density
        val buttonSize: Float = DEFAULT_BUFFER_SIZE * density
    }

    /**
     * When clicked on Open Animation
     */
    private fun openAnimation() {
        val centerX: Float = menuCenterButton.getX()
        val centerY: Float = menuCenterButton.getY()

        val buttonsCount: Int = mButtons.size
        val angleStep = 360f / buttonsCount

        val menuDisplayAnimation = ValueAnimator.ofFloat(0f, mRadius)
        val alphaAnimation = ObjectAnimator.ofFloat(
            menuCenterButton,
            "alpha",
            0.5f
        )
        menuDisplayAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                menuCenterButton.setImageResource(R.drawable.ic_baseline_cancel_24)
                for (view in mButtons) {
                    view.visibility = View.VISIBLE
                }
            }

        })
        if (mOrientation == 1) {
            menuDisplayAnimation.addUpdateListener {
                var i = 1
                for (view in mButtons) {
                    val y = (it.animatedValue as Float * i) / 1.2
                    view.x = centerX
                    view.y = (centerY - y).toFloat()
                    view.scaleX = 1.0f * it.animatedFraction
                    view.scaleY = 1.0f * it.animatedFraction
                    i += 1
                }
            }
        } else {
            menuDisplayAnimation.addUpdateListener {
                var i = 0
                for (view in mButtons) {
                    val angle = angleStep * i - 90
                    val x: Float =
                        cos(Math.toRadians(angle.toDouble())).toFloat() * it.animatedValue as Float
                    val y: Float =
                        sin(Math.toRadians(angle.toDouble())).toFloat() * it.animatedValue as Float
                    view.x = centerX + x
                    view.y = centerY + y
                    view.scaleX = 1.0f * it.animatedFraction
                    view.scaleY = 1.0f * it.animatedFraction
                    i += 1
                }
            }
        }
        menuDisplayAnimation.interpolator = OvershootInterpolator()
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimation, menuDisplayAnimation)
        animatorSet.duration = mOpenAnimationDuration.toLong()
        animatorSet.start()

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                isAnimating = true
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                isAnimating = false
                viewState = MenuState.OPEN
            }
        })
    }

    /**
     * when clicked on Close Button
     */
    private fun closeAnimation() {
        val centerX: Float = menuCenterButton.x
        val centerY: Float = menuCenterButton.y

        val buttonsCount: Int = mButtons.size
        val angleStep = 360f / buttonsCount
        val mCloseAnimation = ValueAnimator.ofFloat(mRadius, 0f)

        val alphaAnimation = ObjectAnimator.ofFloat(
            menuCenterButton,
            "alpha",
            1.0f
        )
        if (mOrientation == 1) {
            mCloseAnimation.addUpdateListener {
                var i = 1
                for (view in mButtons) {
                    // val angle = angleStep * i - 90
                    val y = (it.animatedValue as Float * i) / 1.2
                    view.x = centerX
                    view.y = (centerY-y).toFloat()
                    view.scaleX = 1.0f * 1
                    view.scaleY = 1.0f * 1
                    i += 1
                }
            }
        } else {
            mCloseAnimation.addUpdateListener {
                var i = 0
                for (view in mButtons) {
                    val angle = angleStep * i - 90
                    val x: Float =
                        cos(Math.toRadians(angle.toDouble())).toFloat() * it.animatedValue as Float
                    val y: Float =
                        sin(Math.toRadians(angle.toDouble())).toFloat() * it.animatedValue as Float
                    view.x = centerX + x
                    view.y = centerY + y
                    view.scaleX = 1.0f * 1
                    view.scaleY = 1.0f * 1
                    i += 1
                }
            }
        }

        mCloseAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                // menuCenterButton.rotation = -60f
                menuCenterButton.setImageResource(R.drawable.ic_baseline_menu_24)
                for (view in mButtons) {
                    view.visibility = View.GONE
                }
            }
        })

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimation, mCloseAnimation)
        animatorSet.duration = mCloseAnimationDuration.toLong()
        animatorSet.start()

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                isAnimating = true
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                isAnimating = false
                viewState = MenuState.Close
            }
        })
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val w = View.resolveSizeAndState(2000, widthMeasureSpec, 0)
//        val h = View.resolveSizeAndState(2000, heightMeasureSpec, 0)
//        setMeasuredDimension(w, h)
//    }

    override fun onClick(v: View?) {
        closeAnimation()
        v?.id?.let { onCircleMenuItemClicked?.onMenuItemClicked(it) }
    }

    /**
     * Set Click listener for menu items.
     */
    fun setOnMenuItemClickListener(onCircleMenuItemClicked: OnCircleMenuItemClicked) {
        this.onCircleMenuItemClicked = onCircleMenuItemClicked
    }
}