
//配置代理对象

let proxyObj = {};
proxyObj['/']={
    ws:false,
    target:'http://localhost:8081',  //把拦截到的请求转发到8081上
    changeOrigin: true,
    pathRewrite:{
        '^/':''  //请求地址不重写
    }
}

module.exports = {
    devServer:{
        host:'localhost',
        port:8080,
        proxy: proxyObj
    }
}