package com.dailymenu.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.dailymenu.data.model.Comment
import com.dailymenu.data.model.LearningProgress
import com.dailymenu.data.model.Recipe
import com.dailymenu.data.model.StepImage
import com.dailymenu.ui.components.CommentInput
import com.dailymenu.ui.components.CommentList
import com.dailymenu.ui.components.LearningProgressCard
import com.dailymenu.ui.components.RatingBar
import com.dailymenu.ui.components.ServingsCalculator
import com.dailymenu.ui.components.StepTimer
import com.dailymenu.ui.components.VideoPlayer
import com.dailymenu.ui.theme.*
import com.dailymenu.ui.viewmodel.RecipeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    onNavigateBack: () -> Unit,
    viewModel: RecipeDetailViewModel = viewModel()
) {
    val recipe by viewModel.recipe.collectAsStateWithLifecycle()
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    val learningProgress by viewModel.learningProgress.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val videoProgress by viewModel.videoProgress.collectAsStateWithLifecycle()
    val commentCount by viewModel.commentCount.collectAsStateWithLifecycle()

    var showCommentInput by remember { mutableStateOf(false) }
    var replyToComment by remember { mutableStateOf<Comment?>(null) }

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = recipe?.name ?: "菜谱详情",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (recipe?.isFavorite == true) 
                                Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "收藏",
                            tint = if (recipe?.isFavorite == true) ErrorRed else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundCream
                )
            )
        },
        bottomBar = {
            if (recipe != null) {
                BottomActionBar(
                    isFavorite = recipe?.isFavorite ?: false,
                    onFavoriteClick = { viewModel.toggleFavorite() },
                    onShareClick = { /* TODO: 分享功能 */ },
                    onCommentClick = { showCommentInput = true },
                    commentCount = commentCount
                )
            }
        },
        containerColor = BackgroundCream
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryOrange)
                    }
                }
                error != null -> {
                    ErrorContent(
                        error = error!!,
                        onRetry = { viewModel.refresh() }
                    )
                }
                recipe != null -> {
                    RecipeDetailContent(
                        recipe = recipe!!,
                        comments = comments,
                        learningProgress = learningProgress,
                        videoProgress = videoProgress,
                        onVideoProgressChange = { progress, duration ->
                            viewModel.updateVideoProgress(progress, duration)
                        },
                        onStepCompleted = { stepNumber ->
                            viewModel.markStepCompleted(stepNumber)
                        },
                        onContinueLearning = { viewModel.startLearning() },
                        onLikeComment = { viewModel.likeComment(it) },
                        onReplyComment = { 
                            replyToComment = it
                            showCommentInput = true 
                        },
                        onLoadMoreComments = { /* TODO: 分页加载 */ }
                    )
                }
            }

            // 评论输入弹窗
            if (showCommentInput) {
                CommentInputDialog(
                    replyTo = replyToComment,
                    onDismiss = { 
                        showCommentInput = false
                        replyToComment = null
                    },
                    onSubmit = { content, rating ->
                        viewModel.addComment(content, rating)
                        showCommentInput = false
                        replyToComment = null
                    }
                )
            }
        }
    }
}

@Composable
private fun RecipeDetailContent(
    recipe: Recipe,
    comments: List<Comment>,
    learningProgress: LearningProgress?,
    videoProgress: Long,
    onVideoProgressChange: (Long, Long) -> Unit,
    onStepCompleted: (Int) -> Unit,
    onContinueLearning: () -> Unit,
    onLikeComment: (Long) -> Unit,
    onReplyComment: (Comment) -> Unit,
    onLoadMoreComments: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 视频播放器（如果有视频）
        if (!recipe.videoUrl.isNullOrBlank()) {
            VideoPlayerSection(
                videoUrl = recipe.videoUrl,
                videoProgress = videoProgress,
                onProgressChange = onVideoProgressChange,
                chapters = recipe.videoChapters
            )
        } else {
            // 封面图片
            RecipeImageSection(recipe = recipe)
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 菜谱基本信息
            RecipeBasicInfo(recipe = recipe)

            Spacer(modifier = Modifier.height(16.dp))

            // 学习进度卡片
            if (learningProgress != null || recipe.steps.isNotEmpty()) {
                LearningProgressCard(
                    recipeName = recipe.name,
                    progress = learningProgress,
                    totalSteps = recipe.steps.size,
                    onContinueClick = onContinueLearning
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 食材清单
            SectionTitle("食材清单")
            ServingsCalculator(
                ingredients = recipe.ingredients,
                defaultServings = recipe.servings.coerceIn(1, 20),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 制作步骤
            SectionTitle("制作步骤")
            StepsSection(
                steps = recipe.steps,
                stepImages = recipe.stepImages,
                completedSteps = learningProgress?.completedSteps ?: emptyList(),
                onStepCompleted = onStepCompleted
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 评论区
            SectionTitle("评论 ($commentCount)")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite
                )
            ) {
                CommentList(
                    comments = comments,
                    onLike = onLikeComment,
                    onReply = onReplyComment,
                    onLoadMore = onLoadMoreComments,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun VideoPlayerSection(
    videoUrl: String,
    videoProgress: Long,
    onProgressChange: (Long, Long) -> Unit,
    chapters: List<com.dailymenu.data.model.VideoChapter>
) {
    var currentPosition by remember { mutableLongStateOf(videoProgress) }
    var duration by remember { mutableLongStateOf(0L) }

    Column {
        VideoPlayer(
            videoUrl = videoUrl,
            onProgressChange = { position ->
                currentPosition = position
                onProgressChange(position, duration)
            },
            initialPosition = videoProgress,
            modifier = Modifier.fillMaxWidth()
        )

        // 视频章节列表（如果有章节）
        if (chapters.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "视频章节",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    chapters.forEachIndexed { index, chapter ->
                        ChapterItem(
                            chapter = chapter,
                            index = index + 1,
                            isCurrent = currentPosition >= chapter.startTime * 1000 && 
                                       currentPosition < chapter.endTime * 1000
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterItem(
    chapter: com.dailymenu.data.model.VideoChapter,
    index: Int,
    isCurrent: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (isCurrent) PrimaryOrange else WarmCream
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = index.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = if (isCurrent) SurfaceWhite else TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = chapter.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isCurrent) PrimaryOrange else TextPrimary
            )
            chapter.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Text(
            text = formatDuration(chapter.startTime),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(minutes, secs)
}

@Composable
private fun RecipeImageSection(recipe: Recipe) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(WarmCream),
        contentAlignment = Alignment.Center
    ) {
        if (!recipe.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = PrimaryOrange
            )
        }
    }
}

@Composable
private fun RecipeBasicInfo(recipe: Recipe) {
    Column {
        Text(
            text = recipe.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 评分和评论数
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RatingBar(
                rating = recipe.rating,
                onRatingChange = { },
                enabled = false,
                starSize = 16.dp
            )
            Text(
                text = "${recipe.rating}",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryYellow,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "(${recipe.reviewCount}条评论)",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = recipe.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        // 信息卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceWhite
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoItem(
                    icon = Icons.Default.Schedule,
                    label = "烹饪时间",
                    value = "${recipe.cookingTime}分钟"
                )
                InfoItem(
                    icon = Icons.Default.LocalFireDepartment,
                    label = "热量",
                    value = "${recipe.calories}卡"
                )
                InfoItem(
                    icon = Icons.Default.RestaurantMenu,
                    label = "食材",
                    value = "${recipe.ingredients.size}种"
                )
                InfoItem(
                    icon = Icons.Default.SignalCellularAlt,
                    label = "难度",
                    value = when (recipe.difficulty) {
                        com.dailymenu.data.model.DifficultyLevel.EASY -> "简单"
                        com.dailymenu.data.model.DifficultyLevel.MEDIUM -> "中等"
                        com.dailymenu.data.model.DifficultyLevel.HARD -> "困难"
                    }
                )
            }
        }

        // 标签
        if (recipe.tags.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recipe.tags.forEach { tag ->
                    AssistChip(
                        onClick = { },
                        label = { Text(tag) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = PrimaryOrangeLight.copy(alpha = 0.3f),
                            labelColor = PrimaryOrangeDark
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StepsSection(
    steps: List<String>,
    stepImages: List<String>,
    completedSteps: List<Int>,
    onStepCompleted: (Int) -> Unit
) {
    val stepImagesMap = remember(stepImages) {
        stepImages.mapIndexed { index, imageUrl ->
            index + 1 to StepImage(
                id = index.toLong(),
                recipeId = 0,
                stepNumber = index + 1,
                imageUrl = imageUrl,
                description = null,
                tips = null,
                duration = null
            )
        }.toMap()
    }

    steps.forEachIndexed { index, step ->
        val stepNumber = index + 1
        val stepImage = stepImagesMap[stepNumber]
        val isCompleted = completedSteps.contains(stepNumber)

        StepImageItem(
            stepNumber = stepNumber,
            stepText = step,
            stepImage = stepImage,
            isCompleted = isCompleted,
            onComplete = { onStepCompleted(stepNumber) }
        )

        if (index < steps.size - 1) {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun StepImageItem(
    stepNumber: Int,
    stepText: String,
    stepImage: StepImage?,
    isCompleted: Boolean = false,
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) SuccessGreen.copy(alpha = 0.05f) else SurfaceWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // 步骤编号
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isCompleted) SuccessGreen else PrimaryOrange
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = SurfaceWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = stepNumber.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                    }
                }

                // 步骤内容
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stepText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )

                    stepImage?.description?.let { desc ->
                        if (desc.isNotBlank()) {
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // 完成按钮
                if (!isCompleted) {
                    IconButton(
                        onClick = onComplete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "标记完成",
                            tint = TextSecondary.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // 步骤图片
            stepImage?.imageUrl?.let { imageUrl ->
                if (imageUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "步骤${stepNumber}图片",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // 小贴士
            stepImage?.tips?.let { tip ->
                if (tip.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    TipCard(tip = tip)
                }
            }

            // 计时器
            stepImage?.duration?.let { duration ->
                if (duration > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    StepTimer(durationSeconds = duration)
                }
            }
        }
    }
}

@Composable
private fun TipCard(tip: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = SecondaryYellow.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = SecondaryYellowDark,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BottomActionBar(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onCommentClick: () -> Unit,
    commentCount: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceWhite,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 收藏按钮
            ActionButton(
                icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                label = if (isFavorite) "已收藏" else "收藏",
                tint = if (isFavorite) ErrorRed else TextPrimary,
                onClick = onFavoriteClick
            )

            // 分享按钮
            ActionButton(
                icon = Icons.Default.Share,
                label = "分享",
                onClick = onShareClick
            )

            // 评论按钮
            ActionButton(
                icon = Icons.AutoMirrored.Filled.Comment,
                label = if (commentCount > 0) "评论($commentCount)" else "评论",
                onClick = onCommentClick
            )

            // 开始学习按钮
            Button(
                onClick = onCommentClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryOrange
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("写评论")
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color = TextPrimary
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = tint
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun CommentInputDialog(
    replyTo: Comment?,
    onDismiss: () -> Unit,
    onSubmit: (String, Float?) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (replyTo != null) "回复 ${replyTo.userName}" else "发表评论",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 评分（仅顶层评论）
                if (replyTo == null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "评分：",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        RatingBar(
                            rating = rating,
                            onRatingChange = { rating = it },
                            enabled = true
                        )
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("说点什么...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (content.isNotBlank()) {
                        onSubmit(content, if (rating > 0) rating else null)
                    }
                },
                enabled = content.isNotBlank()
            ) {
                Text("发送")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = ErrorRed
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryOrange
            )
        ) {
            Text("重试")
        }
    }
}

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryOrange,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val hGapPx = 8
        val vGapPx = 8
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        val rowWidths = mutableListOf<Int>()
        val rowHeights = mutableListOf<Int>()

        var rowMeasurables = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var rowWidth = 0
        var rowHeight = 0

        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints)

            if (rowWidth + placeable.width > constraints.maxWidth && rowMeasurables.isNotEmpty()) {
                rows.add(rowMeasurables)
                rowWidths.add(rowWidth)
                rowHeights.add(rowHeight)
                rowMeasurables = mutableListOf()
                rowWidth = 0
                rowHeight = 0
            }

            rowMeasurables.add(placeable)
            rowWidth += placeable.width + hGapPx
            rowHeight = maxOf(rowHeight, placeable.height)
        }

        if (rowMeasurables.isNotEmpty()) {
            rows.add(rowMeasurables)
            rowWidths.add(rowWidth - hGapPx)
            rowHeights.add(rowHeight)
        }

        val height = rowHeights.sum() + (rows.size - 1) * vGapPx

        layout(constraints.maxWidth, height) {
            var y = 0
            rows.forEachIndexed { rowIndex, row ->
                var x = when (horizontalArrangement) {
                    Arrangement.Center -> (constraints.maxWidth - rowWidths[rowIndex]) / 2
                    Arrangement.End -> constraints.maxWidth - rowWidths[rowIndex]
                    else -> 0
                }

                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + hGapPx
                }
                y += rowHeights[rowIndex] + vGapPx
            }
        }
    }
}
