# from django.shortcuts import render
from django.http import HttpResponse
# from django.views import generic
from django.template import loader


def index(request):
    # latest_question_list = Question.objects.order_by('-pub_date')[:5]
    # context = {'latest_question_list': latest_question_list}
    # return render(request, 'polls/index.html', context)

    # context = {}
    # return render(request, 'streetfight/index.html', context)

    template = loader.get_template('index.html')
    context = {}
    return HttpResponse(template.render(context, request))